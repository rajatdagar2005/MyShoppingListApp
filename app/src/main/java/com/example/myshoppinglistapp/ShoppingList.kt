package com.example.myshoppinglistapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ShoppingListapp(){
    // list ie array will contain all objects of shopping item in one index of this list
    var sItems by remember{ mutableStateOf(listOf<ShoppingItem>()) }
    var showDialog by remember { mutableStateOf(false) }
    var itemname by remember { mutableStateOf("") }
    var itemquantity by remember{ mutableStateOf("") }

    // from here we start seriously me
    Column (modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top) {
        Button(onClick = { showDialog=true },modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(text = "add item")
        }
        // this is a lazy column render only items that apper in UI
        // not necessary to use content in it
        // use it as it is used here
        LazyColumn( modifier = Modifier
            // lazy column is a column that contains infinte amount of columns
            .fillMaxSize()
            .padding(16.dp) ){
            items(sItems){
                // as it is shopping item here
                //  ShoppingListItem(it,{},{}) //isme item,on-click , on-delete sab aeega
                // empty curly bracket means on edit don't do anything
                // on delete don't do anything
                item ->  // basically making item a pointer in place of default pointer it

                // agar edit par click kara to shoppingItemEditor vala UI nhi to ShoppingListItem vala UI
                if(item.isEditing){
                    ShoppingItemEditor(item = item, onEditComplete = {
                        editedName,editedQuantity ->
                        sItems = sItems.map{ it.copy(isEditing = false) } // new sItems is copy of sItems with isEditing = false
                        val editedItem = sItems.find{it.id == item.id}
                        editedItem?.let{
                            it.name = editedName
                            it.quantity = editedQuantity
                        }
                    })
                }
                else{
                    ShoppingListItem(item = item,
                        onEditClick = {
                        // finding out which item we are editing
                        sItems = sItems.map { it.copy(isEditing = (it.id==item.id)) }
                    } ,
                        onDeleteClick = {
                        sItems = sItems - item
                    })
                }
            }
        }
    }
    if(showDialog){
        // we have added title manually
        AlertDialog(onDismissRequest = { showDialog=false },
            confirmButton = {
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Button(onClick = {
                                    if(itemname.isNotBlank()){
                                        val newItem = ShoppingItem(
                                            id = sItems.size + 1, //means s.no
                                            name = itemname,
                                            quantity = itemquantity.toInt(),
                                            // not is use isEditing as it is already false
                                        )
                                        sItems = sItems + newItem
                                        showDialog = false
                                        itemname = ""  // resetting item name to empty again
                                        // we can reset quantity as well here , like itemname
                                    }
                                }) {
                                    Text(text = "add")
                                }
                                Button(onClick = { showDialog=false }) {
                                    Text(text = "cancel")
                                }
                            }
            },
            title = { Text(text = "Add shopping Item")},
            text = {
                Column{
                    OutlinedTextField(value = itemname,
                        onValueChange = {
                        itemname = it  // itemname = it means itemname reads iterator that has string
                    }, singleLine = true, modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp))
                    OutlinedTextField(value = itemquantity,
                        onValueChange = {
                            itemquantity = it  // itemname = it means itemname reads iterator that has string
                    }, singleLine = true, modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp))
                }
            }
        )
    }
}

// data class is like a class which has object not function
// as we seen in bank account file
// we have set intially isediting is set to false
data class ShoppingItem(val id:Int , var name:String , var quantity:Int ,var isEditing : Boolean = false){}

@Composable
// this entire function is LAMDA function , input are string(item name) int(item quantity) to EDIT them
// here item is like an object for shopping item
fun ShoppingItemEditor(item: ShoppingItem,onEditComplete: (String,Int) -> Unit){
    var editedName by remember { mutableStateOf(item.name) } // giving default when use it will be overwritten
    var editedQuantity by remember { mutableStateOf(item.quantity.toString()) } // can be edited only in string
    var isEditing by remember { mutableStateOf(item.isEditing) }
    Row (modifier = Modifier
        .fillMaxWidth()
        .background(Color.White).padding(8.dp).border(
        border = BorderStroke(2.dp, Color(0XFF018786)),
        shape = RoundedCornerShape(20))
        ,horizontalArrangement = Arrangement.SpaceEvenly){
        Column{
            // wrap content size is used it takes limited amount of space as needed for the tasks
            BasicTextField(value = editedName , onValueChange = {editedName = it}, singleLine = true, modifier = Modifier
                .wrapContentSize()
                .padding(8.dp))
            BasicTextField(value = editedQuantity , onValueChange = {editedQuantity = it}, singleLine = true, modifier = Modifier
                .wrapContentSize()
                .padding(8.dp))
        }
        Button(onClick = {
            isEditing=false
            onEditComplete(editedName,editedQuantity.toIntOrNull()?:1) // because if it is null we want default value to be 1
        }
        ){
                Text(text = "save")
        }
    }
}

@Composable
// ye vo final hai jo app par show hoga
// this entire function is LAMDA function

fun ShoppingListItem(
    item: ShoppingItem ,//The line (item: ShoppingItem) in the function definition ka matlab hai ki function ek argument ki expectation karta hai jiska naam item hai, aur us argument ka type ShoppingItem hai.
    //  below both are inbuilt lamda function onclick like in button
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    // these above lamda function takes no input and gives no output they only contains code that can be executed
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(
                border = BorderStroke(2.dp, Color(0XFF018786)),
                shape = RoundedCornerShape(20)
            ),
            horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(text = item.name, modifier = Modifier.padding(8.dp))
        Text(text = "QTY : ${item.quantity}",modifier = Modifier.padding(8.dp))
        Row (modifier = Modifier.padding(8.dp)){
            IconButton(onClick = onEditClick) { // this means shoppingListItem will execute its code once we click on it
                Icon(imageVector = Icons.Default.Edit , contentDescription = null)
            }
            IconButton(onClick = onDeleteClick) { // this means shoppingListItem will execute its code once we click on it
                Icon(imageVector = Icons.Default.Delete , contentDescription = null)
            }
        }
    }
}