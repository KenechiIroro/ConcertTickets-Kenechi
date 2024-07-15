package com.uog.concerttickets

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uog.concerttickets.ui.theme.ConcertTicketsTheme
import java.text.NumberFormat
import java.util.*

data class Band(val name: String, val imageRes: Int,val price: Float)

object BandDataSource {
    val bands = listOf(
        Band("Select a Band", R.mipmap.concert, 0.0f),
        Band("Written by Wolves", R.mipmap.written_by_wolves,24.95f),
        Band("Linkin Park", R.mipmap.linkin_park, 63.95f),
        Band("Man with a Mission", R.mipmap.man_with_a_mission, 36.00f),
        Band("Hollywood Undead", R.mipmap.hollywood_undead,125.0f)
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConcertTicketsTheme {
                ConcertTicketApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConcertTicketApp() {
    var selectedBand by remember { mutableStateOf(BandDataSource.bands[0]) }
    var ticketCount by remember { mutableStateOf("") }
    var totalCost by remember { mutableStateOf("") }

    Scaffold(
            topBar = {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(id = R.mipmap.concert_ticket_logo),
                            contentDescription = "App Logo",
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(
                            modifier = Modifier
                                .align(alignment = Alignment.CenterVertically)
                                .background(Color.White))
                        Text(
                            text = "Concert Tickets",
                            fontWeight = FontWeight.Bold ,
                            color = Color(0Xff1e2c41),
                        )
                    }
                },
            )
        },
    ) { padding ->
        Surface(color =MaterialTheme.colorScheme.background,
                modifier = Modifier.padding(padding)){
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                BandSelection(selectedBand) { selectedBand = it }
                Spacer(modifier = Modifier.height(32.dp))
                TicketInput(ticketCount) { ticketCount = it }
                Spacer(modifier = Modifier.height(32.dp))
                CalculateCostButton(selectedBand, ticketCount) { totalCost = it }
                Spacer(modifier = Modifier.height(40.dp))
                CostDisplay(totalCost)
                Spacer(modifier = Modifier.height(32.dp))
                BandImage(selectedBand)
            }
        }
    }
}

@Composable
fun BandSelection(selectedBand: Band, onBandSelected: (Band) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ){
        Box(modifier = Modifier
            .background(Color(0XFFE3E3E1), RoundedCornerShape(8.dp))
            .padding(8.dp)
            .fillMaxWidth()
        ) {
            TextButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = selectedBand.name,
                        color = Color(0Xff1e2c41),
                        fontSize = 20.sp)
                    Icon(
                        imageVector = ImageVector
                            .vectorResource(
                                id = R.drawable.ic_baseline_arrow_drop_down),
                        contentDescription = "Dropdown arrow",
                        tint = Color.Black
                    )
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(Color(0XFFE3E3E6))
                    .width(350.dp)
            ) {
                BandDataSource.bands.forEach { band ->
                    DropdownMenuItem(
                        text = {Text(text = band.name, color = Color(0Xff1e2c41), fontSize = 20.sp)},
                        onClick = {
                        onBandSelected(band)
                        expanded = false
                    })
                }
            }
        }
    }
}

@Composable
fun TicketInput(ticketCount: String, onTicketCountChange: (String) -> Unit) {
    TextField(
        value = ticketCount,
        onValueChange = onTicketCountChange,
        label = { Text("Number of Tickets") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun CalculateCostButton(selectedBand: Band, ticketCount: String, onCalculate: (String) -> Unit) {
    val costPerTicket = selectedBand.price
    val format: NumberFormat = NumberFormat.getCurrencyInstance().apply {
        currency = Currency.getInstance("GBP")
        maximumFractionDigits = 2
        minimumFractionDigits = 2
    }

    Button(
        onClick = {
            val count = ticketCount.toIntOrNull() ?: 0
            if (selectedBand.name == "Select a Band") {
                onCalculate("Please select a band")
            } else if (count <= 0) {
                onCalculate("Enter value greater than 0")
            } else {
                val total = costPerTicket * count
                onCalculate("Cost for ${selectedBand.name} is ${format.format(total)}")
            }
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0Xff1e2c41)),
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)) {
        Text("Find Cost", fontSize = 20.sp)
    }
}

@Composable
fun CostDisplay(totalCost: String) {
    Text(
        text = totalCost,
        fontSize = 20.sp,
        color = Color.Black,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

@Composable
fun BandImage(selectedBand: Band) {
    Image(
        painter = painterResource(id = selectedBand.imageRes),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        alignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun ConcertTicketAppPreview() {
    ConcertTicketsTheme {
        ConcertTicketApp()
    }
}
