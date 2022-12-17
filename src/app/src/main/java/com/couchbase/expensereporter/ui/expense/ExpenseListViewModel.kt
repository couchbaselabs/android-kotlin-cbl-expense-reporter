package com.couchbase.expensereporter.ui.expense

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.couchbase.expensereporter.data.expense.ExpenseRepository
import com.couchbase.expensereporter.models.StandardExpense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ExpenseListViewModel(
    private val repository: ExpenseRepository)
    : ViewModel() {

    var isLoading = mutableStateOf(false)
    var reportId: String = ""
    var expenseFlow: Flow<List<StandardExpense>>? = null

    suspend fun getExpenseReports() {
        viewModelScope.launch(Dispatchers.IO){
            expenseFlow = repository.getExpenses(reportId)
        }
    }

    val delete: (String) -> Boolean = { documentId: String ->
        var didDelete = false
        viewModelScope.launch(Dispatchers.IO){
            didDelete = repository.delete(documentId)
        }
        didDelete
    }

}