package com.example.pokedexandroid

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.BufferedReader
import java.io.InputStreamReader

import android.widget.Button
import android.widget.TextView
import android.widget.EditText
import android.widget.ImageView

class MainActivity : AppCompatActivity() {

    private lateinit var pokemonList: List<Pokedex>
    private lateinit var pokemonImageView: ImageView
    private lateinit var pokemonInfoDisplay: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        pokemonList = loadPokemonData()

        pokemonImageView = findViewById(R.id.pokemonImageView)
        pokemonInfoDisplay = findViewById(R.id.pokemonInfoDisplay)

        val pokemonNameInput: EditText = findViewById(R.id.pokemonNameInput)
        val searchButton: Button = findViewById(R.id.searchButton)

        searchButton.setOnClickListener {
            val nameToSearch = pokemonNameInput.text.toString()
            val foundPokemon = findPokemonByName(nameToSearch)

            if (foundPokemon != null) {
                displayPokemonInfo(foundPokemon)
            } else {
                pokemonInfoDisplay.text = "Pokemon not found."
                pokemonImageView.setImageResource(R.drawable.ic_launcher_foreground)
            }
        }

        pokemonNameInput.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH || keyEvent?.keyCode == android.view.KeyEvent.KEYCODE_ENTER) {
                // Trigger the search logic here
                searchButton.performClick()
                // Return true to indicate that the event has been handled
                true
            } else {
                false
            }
        }
    }

    private fun loadPokemonData(): List<Pokedex> {
        val pokemons = mutableListOf<Pokedex>()
        try {
            val inputStream = assets.open("pokemon.csv")
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            // Skip the header row
            bufferedReader.readLine()

            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                val tokens = line?.split(",")
                if (tokens != null && tokens.size >= 13) {
                    try {
                        val pokemon = Pokedex(
                            name = tokens[1].trim(),
                            type1 = tokens[2].trim(),
                            type2 = tokens[3].trim(),
                            total = tokens[4].trim().toInt(),
                            hp = tokens[5].trim().toInt(),
                            attack = tokens[6].trim().toInt(),
                            defense = tokens[7].trim().toInt(),
                            spAtk = tokens[8].trim().toInt(),
                            spDef = tokens[9].trim().toInt(),
                            speed = tokens[10].trim().toInt(),
                            generation = tokens[11].trim().toInt(),
                            isLegendary = tokens[12].trim().toBoolean()
                        )
                        pokemons.add(pokemon)
                    } catch (e: Exception) {
                        // Log parsing errors if a row is malformed
                        e.printStackTrace()
                    }
                }
            }
            bufferedReader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return pokemons
    }

    private fun getDrawableIdByName(name: String): Int? {
        return try {
            val field = R.drawable::class.java.getField(name)
            field.getInt(null)
        } catch (e: Exception) {
            null
        }
    }

    private fun displayPokemonInfo(pokemon: Pokedex) {
        val imageName = pokemon.name.lowercase()

        val imageResourceId = getDrawableIdByName(imageName)

        if (imageResourceId != null) {
            pokemonImageView.setImageResource(imageResourceId)
        } else {
            pokemonImageView.setImageResource(R.drawable.ic_launcher_foreground)
        }

        val infoText = """
                Name: ${pokemon.name}
                Type: ${pokemon.type1} ${if (pokemon.type2.isNotEmpty()) ", ${pokemon.type2}" else ""}
                Total Stats: ${pokemon.total}
                HP: ${pokemon.hp}
                Attack: ${pokemon.attack}
                Defense: ${pokemon.defense}
                Special Attack: ${pokemon.spAtk}
                Special Defense: ${pokemon.spDef}
                Speed: ${pokemon.speed}
                Generation: ${pokemon.generation}
                Legendary: ${if (pokemon.isLegendary) "Yes" else "No"}
            """.trimIndent()
        pokemonInfoDisplay.text = infoText
    }

    private fun findPokemonByName(name: String): Pokedex? {
        return pokemonList.find { it.name.equals(name, ignoreCase = true) }
    }
}