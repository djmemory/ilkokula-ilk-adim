package com.ilkokulailkadim.app

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.util.Locale
import kotlin.random.Random

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)

        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(navController = navController)
                    }
                    composable("reading") {
                        ReadingCategoryScreen(navController = navController, onSpeak = { text -> speak(text) })
                    }
                    composable("math") {
                        MathCategoryScreen(navController = navController)
                    }
                    composable("turkish") {
                        TurkishCategoryScreen(navController = navController)
                    }
                    composable("quiz") {
                        QuizScreen(navController = navController)
                    }
                    // Mini Oyun Rotaları
                    composable("game_hece") {
                        HeceGameScreen(navController = navController, onSpeak = { speak(it) })
                    }
                    composable("game_toplama") {
                        MathGameScreen(navController = navController)
                    }
                    composable("game_doya") {
                        DoYaGameScreen(navController = navController)
                    }
                }
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale("tr", "TR")
        }
    }

    private fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}

// Renk Paleti (Çocuk Dostu)
val PastelBlue = Color(0xFF4FC3F7)
val PastelGreen = Color(0xFF81C784)
val PastelOrange = Color(0xFFFFB74D)
val PastelPurple = Color(0xFFBA68C8)
val BackgroundCream = Color(0xFFFFFDF9)

data class MenuCategory(val title: String, val icon: String, val color: Color, val route: String)

@Composable
fun HomeScreen(navController: NavController) {
    val categories = listOf(
        MenuCategory("Okuma & Yazma", "📚", PastelBlue, "reading"),
        MenuCategory("Eğlenceli Matematik", "🧮", PastelGreen, "math"),
        MenuCategory("Türkçe Maceraları", "✏️", PastelOrange, "turkish"),
        MenuCategory("Test & Bilgi Yarışması", "⭐", PastelPurple, "quiz")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCream)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "İlkokula İlk Adım",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF2E3D49)
        )
        Text(
            text = "Öğrenmenin En Eğlenceli Yolu!",
            fontSize = 15.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(categories) { cat ->
                BigMenuButton(cat) {
                    navController.navigate(cat.route)
                }
            }
        }
    }
}

@Composable
fun BigMenuButton(cat: MenuCategory, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = cat.color),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = cat.icon, fontSize = 48.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = cat.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ------------------- OKUMA YAZMA EKRANI -------------------
@Composable
fun ReadingCategoryScreen(navController: NavController, onSpeak: (String) -> Unit) {
    val items = listOf("Hece Tablosu Oyunu", "Dikte Oyunu", "Ses Dedektifi", "DoYa (Doğru Yanlış)")
    
    SubMenuScaffold("Okuma & Yazma", navController) {
        LazyVerticalGrid(columns = GridCells.Fixed(1), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(items) { title ->
                ActivityRowCard(title) {
                    when(title) {
                        "Hece Tablosu Oyunu" -> navController.navigate("game_hece")
                        "DoYa (Doğru Yanlış)" -> navController.navigate("game_doya")
                        else -> onSpeak("$title çok yakında açılacak!")
                    }
                }
            }
        }
    }
}

// Hece Tablosu İnteraktif Uygulaması
@Composable
fun HeceGameScreen(navController: NavController, onSpeak: (String) -> Unit) {
    val sesliHarfler = listOf("A", "E", "I", "İ", "O", "Ö", "U", "Ü")
    val sessizHarfler = listOf("B", "C", "D", "K", "L", "M", "N", "T")
    var selectedSessiz by remember { mutableStateOf("L") }

    SubMenuScaffold("Hece Tablosu", navController) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Bir ünsüz harf seç, heceleri dinle!", fontSize = 16.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(10.dp))

            // Sessiz Harf Seçimi
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                sessizHarfler.take(4).forEach { h ->
                    Button(
                        onClick = { selectedSessiz = h },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedSessiz == h) Color(0xFF1E88E5) else Color.LightGray
                        )
                    ) {
                        Text(h, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Heceler Matrisi
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(sesliHarfler) { sesli ->
                    val hece = "\(selectedSessiz\)sesli".lowercase()
                    Card(
                        modifier = Modifier
                            .height(75.dp)
                            .clickable { onSpeak(hece) },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = "\(selectedSessiz +\)sesli = $hece", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ------------------- MATEMATİK EKRANI -------------------
@Composable
fun MathCategoryScreen(navController: NavController) {
    val items = listOf("Hızlı Toplama Oyunu", "Ritmik Sayma", "Çikolata Saat", "Hangisi Büyük?")
    SubMenuScaffold("Eğlenceli Matematik", navController) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items.forEach { title ->
                ActivityRowCard(title) {
                    if (title == "Hızlı Toplama Oyunu") {
                        navController.navigate("game_toplama")
                    }
                }
            }
        }
    }
}

@Composable
fun MathGameScreen(navController: NavController) {
    var num1 by remember { mutableIntStateOf(Random.nextInt(1, 10)) }
    var num2 by remember { mutableIntStateOf(Random.nextInt(1, 10)) }
    var score by remember { mutableIntStateOf(0) }
    var feedback by remember { mutableStateOf("") }

    val correctAnswer = num1 + num2
    val options = remember(num1, num2) {
        (listOf(correctAnswer, correctAnswer + 1, (correctAnswer - 2).coerceAtLeast(1))
            .distinct().shuffled())
    }

    SubMenuScaffold("Toplama Macerası", navController) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
        ) {
            Text("Puan: $score ⭐", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PastelOrange)
            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "\(num1 +\)num2 = ?", fontSize = 48.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(30.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                options.forEach { opt ->
                    Button(
                        onClick = {
                            if (opt == correctAnswer) {
                                score += 10
                                feedback = "Harika! Doğru Cevap 🎈"
                                num1 = Random.nextInt(1, 10)
                                num2 = Random.nextInt(1, 10)
                            } else {
                                feedback = "Tekrar dene! 💪"
                            }
                        },
                        modifier = Modifier.size(75.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = PastelBlue)
                    ) {
                        Text(text = "$opt", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = feedback, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color(0xFF2E7D32))
        }
    }
}

// ------------------- TÜRKÇE EKRANI -------------------
@Composable
fun TurkishCategoryScreen(navController: NavController) {
    val items = listOf("Alfabe Sıralama", "Ünlü - Ünsüz Harfler", "Zıt Anlamlılar", "Kaç Harf Kaç Hece?")
    SubMenuScaffold("Türkçe Maceraları", navController) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items.forEach { title ->
                ActivityRowCard(title) {}
            }
        }
    }
}

// ------------------- DOYA! (DOĞRU / YANLIŞ) OYUNU -------------------
@Composable
fun DoYaGameScreen(navController: NavController) {
    val questions = listOf(
        "\"Elma\" kelimesi 'E' harfi ile başlar." to true,
        "Türk alfabesinde 35 harf vardır." to false,
        "5 + 3 = 8 eder." to true,
        "\"Kitap\" kelimesi 3 hecelidir." to false
    )
    var qIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var showResult by remember { mutableStateOf(false) }

    SubMenuScaffold("DoYa! (Doğru mu Yanlış mı?)", navController) {
        if (!showResult) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Soru \({qIndex + 1} /\){questions.size}", fontSize = 16.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(30.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().height(160.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6))
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = questions[qIndex].first,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    Button(
                        onClick = {
                            if (questions[qIndex].second) score += 25
                            if (qIndex < questions.size - 1) qIndex++ else showResult = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PastelGreen),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f).height(60.dp)
                    ) {
                        Text("DOĞRU 👍", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            if (!questions[qIndex].second) score += 25
                            if (qIndex < questions.size - 1) qIndex++ else showResult = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f).height(60.dp)
                    ) {
                        Text("YANLIŞ 👎", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Tebrikler!", fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                Text("Toplam Puanın: $score", fontSize = 24.sp, color = PastelOrange, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = { navController.popBackStack() }) {
                    Text("Ana Menüye Dön")
                }
            }
        }
    }
}

// ------------------- TEST & DEĞERLENDİRME EKRANI -------------------
@Composable
fun QuizScreen(navController: NavController) {
    SubMenuScaffold("Kim Öğrenmek İster?", navController) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("1. Sınıf Genel Değerlendirme", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))
            ActivityRowCard("1. Seviye Kolay Test") {}
            Spacer(modifier = Modifier.height(8.dp))
            ActivityRowCard("Farklı Olanı Bul Zeka Oyunu") {}
            Spacer(modifier = Modifier.height(8.dp))
            ActivityRowCard("Yazılanı Değil Rengi Söyle") {}
        }
    }
}

// ------------------- YARDIMCI BİLEŞENLER -------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubMenuScaffold(title: String, navController: NavController, content: @Composable () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("◀", fontSize = 20.sp)
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            content()
        }
    }
}

@Composable
fun ActivityRowCard(title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF37474F))
            Text("Oyna ▶", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PastelBlue)
        }
    }
}