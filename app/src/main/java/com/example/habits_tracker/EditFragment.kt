package com.example.habits_tracker

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import com.example.habits_tracker.domain.Habit
import com.example.habits_tracker.ui.OnSaveCallback
import com.example.habits_tracker.ui.view_models.EditViewModel
import kotlinx.android.synthetic.main.fragment_edit.*

class EditFragment : Fragment() {

    private var mainView: View? = null
    private lateinit var editViewModel: EditViewModel
    private lateinit var onSaveCallback: OnSaveCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onSaveCallback = activity as OnSaveCallback
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = inflater.inflate(R.layout.fragment_edit, container, false)
        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val isModeAdd = it.getBoolean(MODE_ADD)
            val habit = it.getParcelable<Habit>(HABIT)
            editViewModel = EditViewModel(isModeAdd, habit)
            if (!isModeAdd) {
                initEditMode(habit)
            }
        }

        saveButton.setOnClickListener {
            if (allFieldsFilled()) {
                onSave()
            }
        }
    }

    companion object {
        private const val MODE_ADD = "mode_add"
        private const val HABIT = "habit"

        @JvmStatic
        fun newInstance(isModeAdd: Boolean, habit: Habit? = null) =
            EditFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(MODE_ADD, isModeAdd)
                    putParcelable(HABIT, habit)
                }
            }
    }

    private fun initEditMode(habit: Habit?) {
        if (habit != null) {
            titleTextField.setText(habit.title)
            descriptionTextField.setText(habit.description)
            prioritySpinner.setSelection(habit.priority - 1)
            typeRadioGroup.check(if (habit.isGood) R.id.good else R.id.bad)
            countTextField.setText(habit.count.toString())
            periodicityTextField.setText(habit.periodicity.toString())
        }
    }

    private fun allFieldsFilled(): Boolean {
        val requiredTextViews = listOf(
            titleTextField, countTextField, periodicityTextField
        )
        if (requiredTextViews.all { !TextUtils.isEmpty(it.text) }) {
            return true
        }
        requiredTextViews.first { TextUtils.isEmpty(it.text) }.apply {
            error = "Обязательно к заполнению"
            requestFocus()
        }
        return false
    }

    private fun onSave() = Habit(
        titleTextField.text.toString(),
        descriptionTextField.text.toString(),
        prioritySpinner.selectedItem.toString().toInt(),
        (mainView?.findViewById<RadioButton>(typeRadioGroup.checkedRadioButtonId)?.text.toString()
                == resources.getString(R.string.good)),
        countTextField.text.toString().toInt(),
        periodicityTextField.text.toString().toInt()
    ).let {
        editViewModel.saveHabit(it)
        onSaveCallback.onSaveHabit()
    }
}