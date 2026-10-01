package com.npos.app.ui.customers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.npos.app.data.model.Customer

@Composable
fun CustomersScreen(
    customers: List<Customer>,
    isSaving: Boolean = false,
    onAddCustomer: (Customer) -> Unit = {},
    onUpdateCustomer: (Customer) -> Unit = {},
    onDeleteCustomer: (Customer) -> Unit = {},
    onRefresh: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var customerToEdit by remember { mutableStateOf<Customer?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var customerToDelete by remember { mutableStateOf<Customer?>(null) }

    val filteredCustomers = remember(customers, searchQuery) {
        if (searchQuery.isBlank()) customers
        else customers.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.phone.contains(searchQuery, ignoreCase = true) ||
                    it.code.contains(searchQuery, ignoreCase = true) ||
                    it.address.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("add_customer_fab")
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Add Customer")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Header with Count Badge & Refresh Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Customer Management",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.DarkGray
                    )
                    Text(
                        text = "ဖောက်သည်များ စီမံခန့်ခွဲမှု (စုစုပေါင်း: ${customers.size} ဦး)",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier.testTag("refresh_customers_button")
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh Customers",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search name / phone / code / address...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("customer_search_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredCustomers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFE9ECEF),
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, contentDescription = "Empty", tint = Color.Gray, modifier = Modifier.size(32.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No customers found", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.DarkGray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("ဖောက်သည်အသစ် ထည့်သွင်းရန် '+ Add New' ကိုနှိပ်ပါ", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredCustomers, key = { it.id }) { customer ->
                        CustomerItemCard(
                            customer = customer,
                            onEdit = { customerToEdit = customer },
                            onDelete = { customerToDelete = customer }
                        )
                    }
                }
            }
        }
    }

    // Add Customer Dialog
    if (showAddDialog) {
        CustomerEditDialog(
            customer = null,
            isSaving = isSaving,
            onDismiss = { showAddDialog = false },
            onSave = { newCust ->
                onAddCustomer(newCust)
                showAddDialog = false
            }
        )
    }

    // Edit Customer Dialog
    customerToEdit?.let { editTarget ->
        CustomerEditDialog(
            customer = editTarget,
            isSaving = isSaving,
            onDismiss = { customerToEdit = null },
            onSave = { updated ->
                onUpdateCustomer(updated)
                customerToEdit = null
            }
        )
    }

    // Delete Confirmation Dialog
    customerToDelete?.let { delTarget ->
        AlertDialog(
            onDismissRequest = { customerToDelete = null },
            title = {
                Text(text = "Delete Customer (ဖျက်ရန် အတည်ပြုပါ)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Text(
                    text = "Customer '${delTarget.name}' ကို ဖျက်ရန် သေချာပါသလား? ဤလုပ်ဆောင်ချက်ကို ပြန်လည်ပြင်ဆင်၍ မရနိုင်ပါ။",
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteCustomer(delTarget)
                        customerToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC3545)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Delete (ဖျက်မည်)", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { customerToDelete = null },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancel (မလုပ်တော့ပါ)")
                }
            }
        )
    }
}

@Composable
private fun CustomerItemCard(
    customer: Customer,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isDefaultCustomer = customer.code == "123456789" || customer.id == "123456789"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("customer_card_${customer.code}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Surface(
                shape = CircleShape,
                color = if (isDefaultCustomer) Color(0xFFE2E8F0) else MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (customer.name.isNotBlank()) {
                        val initials = customer.name.take(2).uppercase()
                        Text(
                            text = initials,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (isDefaultCustomer) Color.DarkGray else MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Customer",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = customer.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    if (isDefaultCustomer) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFE0E7FF)
                        ) {
                            Text(
                                text = "Default",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3730A3),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Phone
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Phone",
                        tint = Color.Gray,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = customer.phone.ifEmpty { "N/A" },
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }

                // Address (if present)
                if (customer.address.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Address",
                            tint = Color.Gray,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = customer.address,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Code: ${customer.code}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            // Action Buttons
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(36.dp).testTag("edit_customer_${customer.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Customer",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (!isDefaultCustomer) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp).testTag("delete_customer_${customer.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Customer",
                            tint = Color(0xFFDC3545),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomerEditDialog(
    customer: Customer?,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (Customer) -> Unit
) {
    val isEdit = customer != null
    var name by remember { mutableStateOf(customer?.name.orEmpty()) }
    var phone by remember { mutableStateOf(customer?.phone.orEmpty()) }
    var address by remember { mutableStateOf(customer?.address.orEmpty()) }
    var code by remember {
        mutableStateOf(customer?.code ?: System.currentTimeMillis().toString())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEdit) "Edit Customer (ဖောက်သည်ပြင်ဆင်မည်)" else "Add New Customer (ဖောက်သည်အသစ်ထည့်မည်)",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Customer Name (အမည်) *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number (ဖုန်းနံပါတ်)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address (လိပ်စာ/မြို့နယ်)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Customer Code (ကုဒ်)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val toSave = customer?.copy(
                            name = name.trim(),
                            phone = phone.trim(),
                            address = address.trim(),
                            code = code.trim()
                        ) ?: Customer(
                            id = "",
                            name = name.trim(),
                            phone = phone.trim(),
                            address = address.trim(),
                            code = code.trim()
                        )
                        onSave(toSave)
                    }
                },
                enabled = !isSaving && name.isNotBlank(),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                } else {
                    Text(if (isEdit) "Update" else "Save")
                }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cancel")
            }
        }
    )
}
