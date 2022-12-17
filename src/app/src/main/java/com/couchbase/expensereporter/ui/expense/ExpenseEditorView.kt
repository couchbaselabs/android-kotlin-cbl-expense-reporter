package com.couchbase.expensereporter.ui.expense

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.couchbase.expensereporter.ui.components.AppBar
import com.couchbase.expensereporter.ui.components.DatePicker
import com.couchbase.expensereporter.ui.theme.ExpenseReporterTheme
import com.couchbase.expensereporter.ui.theme.*
import kotlinx.coroutines.CoroutineScope

@Composable
fun ExpenseEditorView(
    viewModel: ExpenseEditorViewModel,
    navigateUp: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    scope: CoroutineScope = rememberCoroutineScope()){

    ExpenseReporterTheme {
        // A surface container using the 'background' color from the theme
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                AppBar(title = "Expense Editor",
                    navigationIcon = Icons.Filled.ArrowBack,
                    navigationOnClick = { navigateUp() })
            }
        )
        {
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            )
            {
                ExpenseEditor(
                    viewModel.parentExpensesTypeState,
                    viewModel.onParentExpenseTypeChange,
                    viewModel.selectedParentIndexState.value,
                    viewModel.childExpensesTypeState,
                    viewModel.onChildExpenseTypeChange,
                    viewModel.selectedChildIndexState.value,
                    viewModel.descriptionState.value,
                    viewModel.onDescriptionChanged,
                    viewModel.dateState.value,
                    viewModel.onDateChanged,
                    viewModel.amountState.value,
                    viewModel.onAmountChanged,
                    viewModel.onSave,
                    viewModel.errorMessageState.value
                )
            }
        }
    }
}

@Composable
fun ExpenseEditor(
    parentExpenseTypes: List<String>,
    onParentExpenseTypeChange: (Int) -> Unit,
    selectedParentIndex: Int,
    childExpenseTypes: List<String>,
    onChildExpenseTypeChange: (Int) -> Unit,
    selectedChildIndex: Int,
    description: String,
    onDescriptionChange: (String) -> Unit,
    date: String,
    onDateChanged: (Long?) -> Unit,
    amount: Double,
    onAmountChanged: (String) -> Unit,
    onSave: () -> Unit,
    errorMessage: String
){
    var expenseTypeParentExpanded by remember { mutableStateOf(false)}
    var expenseTypeChildExpanded by remember { mutableStateOf(false)}

    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(16.dp)) {
        item {
            Text("Select Expense Type:",
                modifier = Modifier.padding(bottom = 10.dp),
                style = TextStyle(fontSize = 18.sp),)
        }
        //parent drop down list
        item {
            Box(modifier = Modifier
                .background(color = Red500)
                .border(width = 1.dp, color = Red900)
                .padding(top = 8.dp, bottom = 8.dp, start = 4.dp, end = 4.dp)
                .fillMaxSize()
                .wrapContentSize(Alignment.TopStart)) {
                Text(
                    parentExpenseTypes[selectedParentIndex],
                    style = TextStyle(color = Color.White, fontSize = 18.sp),
                    modifier = Modifier
                        .clickable(
                        onClick = {
                            expenseTypeParentExpanded = true
                        })
                )
                DropdownMenu(
                    expanded = expenseTypeParentExpanded,
                    onDismissRequest = { expenseTypeParentExpanded = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Red200
                        )
                ) {
                    parentExpenseTypes.forEachIndexed { index, parentExpenseType ->
                        DropdownMenuItem(onClick = {
                            onParentExpenseTypeChange(index)
                            expenseTypeParentExpanded = false
                        }) {
                            Text(text = parentExpenseType)
                        }
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.padding(12.dp))
        }
        //child drop down list
        item {
            Box(modifier = Modifier
                .background(color = Red500)
                .border(width = 1.dp, color = Red900)
                .padding(top = 8.dp, bottom = 8.dp, start = 4.dp, end = 4.dp)
                .fillMaxSize()
                .wrapContentSize(Alignment.TopStart)
            ){
                Text(
                    childExpenseTypes[selectedChildIndex],
                    style = TextStyle(color = Color.White, fontSize = 18.sp),
                    modifier = Modifier
                        .clickable(
                            onClick = {
                                expenseTypeChildExpanded = true
                            })
                )
                DropdownMenu(
                    expanded = expenseTypeChildExpanded,
                    onDismissRequest = { expenseTypeChildExpanded = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Red200
                        )
                ) {
                    childExpenseTypes.forEachIndexed { index, childExpenseType ->
                        DropdownMenuItem(onClick = {
                            onChildExpenseTypeChange(index)
                            expenseTypeChildExpanded = false
                        }) {
                            Text(text = childExpenseType)
                        }
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.padding(12.dp))
        }
        item {
            Text("Expense Date:",
                modifier = Modifier.padding(bottom = 1.dp),
                style = TextStyle(fontSize = 18.sp),)
        }
        item {
            DatePicker(selectedDate = date, onDateChanged = onDateChanged)
        }
        item {
            Spacer(modifier = Modifier.padding(8.dp))
        }
        item {
            TextField(
                value = amount.toString(),
                onValueChange = onAmountChanged,
                label = { Text("Currency Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            )
        }

        item {
            Spacer(modifier = Modifier.padding(8.dp))
        }
        item {
            TextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text("Description") },
                maxLines = 5,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            )
        }
        item {
            Column(
                Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(modifier = Modifier
                    .padding(top = 24.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Red500),
                    onClick = {
                        onSave()
                    })
                {
                    Text("Save",
                        color = Color.White,
                        style = MaterialTheme.typography.h5)
                }
            }
        }
        if (errorMessage.isNotEmpty()){
            item {
                Text(
                    modifier = Modifier.padding(top = 12.dp),
                    text = errorMessage,
                    style = MaterialTheme.typography.subtitle1,
                    color = MaterialTheme.colors.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExpenseEditorPreview() {
    val parentExpenseTypes = listOf("Communications", "Meals and Entertainment", "Office")
    val onParentExpenseTypeChange: (Int) -> Unit = {}
    val selectedParentIndex: Int = 0
    val childExpenseType = listOf("Cell Phone & Internet", "Internet Access")
    val onChildExpenseTypeChange: (Int) -> Unit = {}
    val selectedChildIndex: Int = 0
    val description = "Test Description"
    val onDescriptionChange: (String) -> Unit = {}
    val date = "12/25/2022"
    val onDateChanged: (Long?) -> Unit = {}
    val amount = 0.00
    val onAmountChanged: (String) -> Unit = {}
    val onSave: () -> Unit = {}
    val errorMessage = ""

    ExpenseReporterTheme {
        Surface(
            color = MaterialTheme.colors.background,
            modifier = Modifier.fillMaxSize()
        ) {
            ExpenseEditor(
                parentExpenseTypes,
                onParentExpenseTypeChange,
                selectedParentIndex,
                childExpenseType,
                onChildExpenseTypeChange,
                selectedChildIndex,
                description,
                onDescriptionChange,
                date,
                onDateChanged,
                amount,
                onAmountChanged,
                onSave,
                errorMessage
            )
        }
    }
}