package br.com.brunoccbertolini.to_dolistsanta.ui

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.brunoccbertolini.to_dolistsanta.databinding.ActivityAddTaskBinding
import br.com.brunoccbertolini.to_dolistsanta.datasource.TaskDataSource
import br.com.brunoccbertolini.to_dolistsanta.extensions.format
import br.com.brunoccbertolini.to_dolistsanta.extensions.text
import br.com.brunoccbertolini.to_dolistsanta.model.Task
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(TASK_ID)){
            val taskId = intent.getIntExtra(TASK_ID, 0)
            TaskDataSource.findbyId(taskId)?.let {
                binding.tilTitle.text = it.title
                binding.tilHour.text = it.hour
                binding.tilData.text = it.date
            }
        }

        insertListeners()
    }

    private fun insertListeners() {
        binding.tilData.editText?.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.addOnPositiveButtonClickListener {
                val timeZone = TimeZone.getDefault()
                val offset = timeZone.getOffset(Date().time) * -1
                binding.tilData.text = Date(it + offset).format()
            }
            datePicker.show(supportFragmentManager, "DATE_PICKER_TAG")
        }

        binding.tilHour.editText?.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .build()
            timePicker.addOnPositiveButtonClickListener{
                val minute = if (timePicker.minute in 0..9) "0${timePicker.minute}" else timePicker.minute
                val hour = if (timePicker.hour in 0..9) "0${timePicker.hour}" else timePicker.hour
                binding.tilHour.text = "$hour:$minute"
            }
            timePicker.show(supportFragmentManager, null)
        }
        binding.btnCreateTask.setOnClickListener{
            val task = Task(
                title = binding.tilTitle.text,
                date = binding.tilData.text,
                hour = binding.tilHour.text,
                id = intent.getIntExtra(TASK_ID, 0)
            )
            TaskDataSource.insertTask(task)
            setResult(Activity.RESULT_OK)
            finish()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    companion object{
        const val TASK_ID = "task_id"
    }
}