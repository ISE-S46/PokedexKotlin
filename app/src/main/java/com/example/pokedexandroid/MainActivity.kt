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

class MainActivity : AppCompatActivity() {

    private lateinit var pokemonList: List<Pokedex>

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

        val pokemonNameInput: EditText = findViewById(R.id.pokemonNameInput)
        val searchButton: Button = findViewById(R.id.searchButton)
        val pokemonInfoDisplay: TextView = findViewById(R.id.pokemonInfoDisplay)

        searchButton.setOnClickListener {
            val nameToSearch = pokemonNameInput.text.toString()
            val foundPokemon = findPokemonByName(nameToSearch)

            if (foundPokemon != null) {
                val infoText = """
                Name: ${foundPokemon.name}
                Type: ${foundPokemon.type1} ${if (foundPokemon.type2.isNotEmpty()) ", ${foundPokemon.type2}" else ""}
                Total Stats: ${foundPokemon.total}
                HP: ${foundPokemon.hp}
                Attack: ${foundPokemon.attack}
                Defense: ${foundPokemon.defense}
                Special Attack: ${foundPokemon.spAtk}
                Special Defense: ${foundPokemon.spDef}
                Speed: ${foundPokemon.speed}
                Generation: ${foundPokemon.generation}
                Legendary: ${if (foundPokemon.isLegendary) "Yes" else "No"}
            """.trimIndent()
                pokemonInfoDisplay.text = infoText
            } else {
                pokemonInfoDisplay.text = "Pokemon not found."
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

    private fun findPokemonByName(name: String): Pokedex? {
        // Ignore case for a better user experience
        return pokemonList.find { it.name.equals(name, ignoreCase = true) }
    }
}