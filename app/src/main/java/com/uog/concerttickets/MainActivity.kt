package com.uog.concerttickets

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.uog.concerttickets.ui.theme.ConcertTicketsTheme
import com.uog.concerttickets.ui.theme.TicketBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Calendar
import java.util.Currency

data class Band(val name: String, val imageRes: Int, val price: Float)

object BandDataSource {
    val bands = listOf(
        Band("Select a Band", R.mipmap.concert, 0.0f),
        Band("Written by Wolves", R.mipmap.written_by_wolves, 24.95f),
        Band("Linkin Park", R.mipmap.linkin_park, 63.95f),
        Band("Man with a Mission", R.mipmap.man_with_a_mission, 36.00f),
        Band("Hollywood Undead", R.mipmap.hollywood_undead, 125.0f),
        Band("Electric Call Boy", R.mipmap.ecb, 125.0f),
        Band("Self Deception", R.mipmap.self_deception, 125.0f)
    )
}

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Help : Screen("help")
}

/**
* This ViewModel is used to store and manage the app's data.
* It keeps the UI state separate from the user interface so the code
* is easier to understand and maintain.
* Coroutines are used here so calculations do not block the main UI thread.
*/

class TicketViewModel : ViewModel() {

    var selectedBand by mutableStateOf(BandDataSource.bands[0])
        private set

    var ticketCount by mutableStateOf("")
        private set

    var totalCost by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun onBandSelected(band: Band) {
        selectedBand = band
        // maybe clear previous result when band changes
        totalCost = ""
    }

    fun onTicketCountChange(value: String) {
        ticketCount = value.filter { it.isDigit() }
    }

    fun calculateTotal() {
        val count = ticketCount.toIntOrNull() ?: 0

        if (selectedBand.name == "Select a Band") {
            totalCost = "Please select a band"
            return
        }
        if (count <= 0) {
            totalCost = "Enter value greater than 0"
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                delay(600)

                val format: NumberFormat = NumberFormat.getCurrencyInstance().apply {
                    currency = Currency.getInstance("GBP")
                    maximumFractionDigits = 2
                    minimumFractionDigits = 2
                }

                val base = selectedBand.price * count

                // Apply a bulk discount if the user buys 5 or more tickets
                val discountRate = if (count >= 5) 0.10f else 0.0f
                val discount = base * discountRate

                // Add a small service fee for each ticket
                val serviceFee = 1.25f * count

                // Increase the price slightly during busy peak times (evenings and weekends)
                val peakMultiplier = if (isPeakTime()) 1.05f else 1.0f

                val total = ((base - discount) + serviceFee) * peakMultiplier

                totalCost = buildString {
                    append("Band: ${selectedBand.name}\n")
                    append("Tickets: $count\n")
                    append("Base: ${format.format(base)}\n")
                    if (discountRate > 0f) append("Discount (10%): -${format.format(discount)}\n")
                    append("Service fee: +${format.format(serviceFee)}\n")
                    if (peakMultiplier > 1f) append("Peak pricing (5%): applied\n")
                    append("\nTotal: ${format.format(total)}")
                }
            } finally {
                isLoading = false
            }
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConcertTicketsTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ) {
                    composable(Screen.Home.route) {
                        ConcertTicketApp(
                            onHelpClick = { navController.navigate(Screen.Help.route) }
                        )
                    }
                    composable(Screen.Help.route) {
                        HelpScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConcertTicketApp(
    onHelpClick: () -> Unit,
    vm: TicketViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(id = R.mipmap.concert_ticket_logo),
                            contentDescription = "App Logo",
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Concert Tickets",
                            fontWeight = FontWeight.Bold,
                            color = TicketBlue
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onHelpClick) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Help"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                BandSelection(
                    selectedBand = vm.selectedBand,
                    onBandSelected = vm::onBandSelected
                )

                Spacer(modifier = Modifier.height(24.dp))

                TicketInput(
                    ticketCount = vm.ticketCount,
                    onTicketCountChange = vm::onTicketCountChange
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { vm.calculateTotal() },
                    enabled = !vm.isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0Xff1e2c41)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp)
                ) {
                    Text(if (vm.isLoading) "Calculating..." else "Find Cost", fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (vm.isLoading) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Calculating…")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                CostDisplay(vm.totalCost)

                Spacer(modifier = Modifier.height(24.dp))
                BandImage(vm.selectedBand)
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
    ) {
        Box(
            modifier = Modifier
                .background(Color(0XFFE3E3E6), RoundedCornerShape(8.dp))
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
                    Text(
                        text = selectedBand.name,
                        color = Color(0Xff1e2c41),
                        fontSize = 20.sp
                    )
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_arrow_drop_down),
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
                    .fillMaxWidth()
            ) {
                BandDataSource.bands.forEach { band ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = band.name,
                                color = Color(0Xff1e2c41),
                                fontSize = 20.sp
                            )
                        },
                        onClick = {
                            onBandSelected(band)
                            expanded = false
                        }
                    )
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

fun isPeakTime(): Boolean {
    val cal = Calendar.getInstance()
    val day = cal.get(Calendar.DAY_OF_WEEK)
    val hour = cal.get(Calendar.HOUR_OF_DAY)

    val isFri = day == Calendar.FRIDAY
    val isSat = day == Calendar.SATURDAY

    return (isFri || isSat) && hour >= 18
}

@Composable
fun CostDisplay(totalCost: String) {
    if (totalCost.isBlank()) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = totalCost,
            modifier = Modifier.padding(16.dp),
            fontSize = 16.sp
        )
    }
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
            .height(280.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help & Instructions") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "How to Use the Concert Tickets App",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text("1. Select a band from the dropdown menu.")
            Text("2. Enter the number of tickets you want to purchase.")
            Text("3. Tap 'Find Cost' to calculate the total price.")
            Text("4. Discount applies for 5+ tickets.")
            Text("5. Peak pricing may apply on Fri/Sat after 18:00.")

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Pricing Rules", fontWeight = FontWeight.Bold)
            Text("• 10% discount for 5 or more tickets")
            Text("• Service fee (£1.25) per ticket")
            Text("• Peak pricing (+5%) Fri/Sat after 18:00")

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to App")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConcertTicketAppPreview() {
    ConcertTicketsTheme {
        ConcertTicketApp(onHelpClick = {})
    }
}
