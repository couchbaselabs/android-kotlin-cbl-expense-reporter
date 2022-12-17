package com.couchbase.expensereporter.ui.expense

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.couchbase.expensereporter.data.expense.ExpenseRepository
import com.couchbase.expensereporter.data.expenseTypes.ExpenseTypeRepository
import com.couchbase.expensereporter.models.ExpenseType
import com.couchbase.expensereporter.models.ExpenseTypes
import com.couchbase.expensereporter.models.StandardExpense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ExpenseEditorViewModel(
    private val expenseTypeRepository: ExpenseTypeRepository,
    private val expenseRepository: ExpenseRepository)
    : ViewModel() {

    private var expenseReportState = mutableStateOf<StandardExpense?>(null)
    var expenseReportIdState = mutableStateOf("")
    var reportIdState = mutableStateOf("")
    var descriptionState = mutableStateOf("")
    var amountState = mutableStateOf(0.00)
    var dateState = mutableStateOf("Select Date")

    //track selection lists
    private lateinit var expenseTypes: List<ExpenseType>
    var parentExpensesTypeState = mutableStateListOf("")
    var childExpensesTypeState = mutableStateListOf("")
    var selectedParentIndexState = mutableStateOf(0)
    var selectedChildIndexState = mutableStateOf(0)

    val errorMessageState = mutableStateOf("")

    init {
        viewModelScope.launch {
            val expenseTypesCol = expenseTypeRepository.get()
            if (expenseTypesCol.isNotEmpty()) {
                expenseTypes = expenseTypesCol[0].expenseTypes
                val parentExpenseTypes = mutableListOf("")
                expenseTypes.map{ parentExpenseTypes.add(it.name) }

                withContext(Dispatchers.Main) {
                    parentExpenseTypes.removeAt(0)
                    parentExpensesTypeState = parentExpenseTypes.toMutableStateList()
                    childExpensesTypeState = expenseTypes[0].subTypes.toMutableStateList()
                }
            }
        }
    }

    var navigateUpCallback: () -> Unit = { }

    val onDateChanged: (Long?) -> Unit = { date ->
        date?.let { theDate ->
            dateState.value = dateFormatter(theDate)
            val r = expenseReportState.value?.copy(date = date)
            expenseReportState.value = r
        }
    }

    val onDescriptionChanged: (String) -> Unit = { newValue ->
        val r = expenseReportState.value?.copy(description = newValue)
        expenseReportState.value = r
        descriptionState.value = newValue
    }

    val onAmountChanged: (String) -> Unit = { newValue ->
        var amountChanged = newValue.toDoubleOrNull()
        amountChanged?.let { amount ->
            val r = expenseReportState.value?.copy(amount = amount)
            expenseReportState.value = r
            amountState.value = amount
        }

    }

    val onParentExpenseTypeChange: (Int) -> Unit = { newValue ->
        if (newValue <= parentExpensesTypeState.size) {
            selectedParentIndexState.value = newValue

            //update expense type category
            val r = expenseReportState.value?.copy(expenseTypeCategory = parentExpensesTypeState[newValue])
            expenseReportState.value = r

            //load child expenses
            childExpensesTypeState = expenseTypes[newValue].subTypes.toMutableStateList()
            selectedChildIndexState.value = 0
        }
    }

    val onChildExpenseTypeChange: (Int) -> Unit = { newValue ->
        if (newValue <= childExpensesTypeState.size) {
            selectedChildIndexState.value = newValue
            //update expense type
            val r = expenseReportState.value?.copy(expenseType = childExpensesTypeState[newValue])
            expenseReportState.value = r
        }
    }

    private fun dateFormatter(milliseconds: Long): String {
        val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.US)
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliseconds
        return formatter.format(calendar.time)
    }

    fun loadExpense() {
        viewModelScope.launch(Dispatchers.IO) {
            val expenseReport = expenseRepository.get(
                reportId = reportIdState.value,
                documentId = expenseReportIdState.value
            )
            withContext(Dispatchers.Main) {
                expenseReportState.value = expenseReport
                expenseReport.date?.let { date ->
                    val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.US)
                    dateState.value = formatter.format(date)
                }
                if (expenseReport.description.isNotBlank()) {
                    descriptionState.value = expenseReport.description
                }
                amountState.value = expenseReport.amount
                
                if (expenseReport.expenseTypeCategory.isNotBlank()) {
                    selectedParentIndexState.value =
                        parentExpensesTypeState.indexOf(expenseReport.expenseTypeCategory)
                    //load child expenses
                    childExpensesTypeState =
                        expenseTypes[selectedParentIndexState.value].subTypes.toMutableStateList()
                    selectedChildIndexState.value =
                        childExpensesTypeState.indexOf(expenseReport.expenseType)
                } else {
                    //default expense type to first item in both lists
                    val r = expenseReportState.value?.copy(
                        expenseTypeCategory = parentExpensesTypeState[0],
                        expenseType = childExpensesTypeState[0]
                    )
                    expenseReportState.value = r
                }
            }
        }
    }

    val onSave: () -> Unit = {
        viewModelScope.launch(Dispatchers.IO) {
            expenseReportState.value?.let { standardExpense ->
                if (standardExpense.expenseType.isNotBlank())
                {
                    errorMessageState.value = ""
                    expenseRepository.save(standardExpense)
                    withContext(Dispatchers.Main) {
                        navigateUpCallback()
                    }
                }
                else {
                    errorMessageState.value = "Error - no expense type selected, can't save without setting"
                }
            }
        }
    }
}