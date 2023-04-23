package com.example.testcompose

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.testcompose.ui.theme.TestComposeTheme

class temp {
}
@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TestComposeTheme {
        Greeting("Android")
    }
}