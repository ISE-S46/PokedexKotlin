package com.example.pokedexandroid

import android.os.Bundle
import android.view.View
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
import android.widget.LinearLayout

class MainActivity : AppCompatActivity() {

    private lateinit var pokemonList: List<Pokedex>
    private lateinit var pokemonImageView: ImageView
    private lateinit var pokemonInfoDisplay: TextView
    private lateinit var pokemonNameInput: EditText // Also need to make this a class member
    private lateinit var searchButton: Button

    private lateinit var pokemonNameDisplay: TextView
    private lateinit var pokemonType1Display: TextView
    private lateinit var pokemonType2Display: TextView
    private lateinit var typeBadgesContainer: LinearLayout
    private lateinit var baseStatsTitle: TextView
    private lateinit var pokemonStatsContainer: LinearLayout

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
        pokemonNameInput = findViewById(R.id.pokemonNameInput)
        searchButton = findViewById(R.id.searchButton)

        pokemonNameDisplay = findViewById(R.id.pokemonNameDisplay)
        pokemonType1Display = findViewById(R.id.pokemonType1Display)
        pokemonType2Display = findViewById(R.id.pokemonType2Display)
        typeBadgesContainer = findViewById(R.id.typeBadgesContainer)
        baseStatsTitle = findViewById(R.id.baseStatsTitle)
        pokemonStatsContainer = findViewById(R.id.pokemonStatsContainer)

        searchButton.setOnClickListener {
            performSearch()
        }

        pokemonNameInput.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH || keyEvent?.keyCode == android.view.KeyEvent.KEYCODE_ENTER) {
                // Trigger the search logic
                performSearch()
                // Return true to indicate that the event has been handled
                true
            } else {
                false
            }
        }

        hidePokemonDisplayElements()
    }

    private fun performSearch() {
        val nameToSearch = pokemonNameInput.text.toString()
        val foundPokemon = findPokemonByName(nameToSearch)

        if (foundPokemon != null) {
            displayPokemonInfo(foundPokemon)
            showPokemonDisplayElements()
            // Hide the general info display if specific elements are shown
            pokemonInfoDisplay.visibility = View.VISIBLE
        } else {
            pokemonInfoDisplay.text = "Pokemon not found."
            pokemonInfoDisplay.visibility = View.VISIBLE // Show general error message
            pokemonImageView.setImageResource(R.drawable.pokeball)
            hidePokemonDisplayElements() // Hide specific details
        }
    }

    private fun findPokemonByName(name: String): Pokedex? {
        return pokemonList.find { it.name.equals(name, ignoreCase = true) }
    }

    private fun hidePokemonDisplayElements() {
        pokemonNameDisplay.visibility = View.INVISIBLE
        pokemonType1Display.visibility = View.INVISIBLE
        pokemonType2Display.visibility = View.INVISIBLE
        pokemonInfoDisplay.visibility = View.INVISIBLE
    }

    private fun showPokemonDisplayElements() {
        // Elements like image and name will be set to VISIBLE in displayPokemonInfo
        // This function ensures the main containers are visible
        // For future feature update, empty for now
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
        val imageName = pokemon.name.lowercase().replace(" ", "_").replace(".", "").replace("'", "")

        val imageResourceId = getDrawableIdByName(imageName)

        if (imageResourceId != null) {
            pokemonImageView.setImageResource(imageResourceId)
        } else {
            pokemonImageView.setImageResource(R.drawable.ic_launcher_foreground)
        }

        // Set Pokemon Name
        pokemonNameDisplay.text = pokemon.name
        pokemonNameDisplay.visibility = View.VISIBLE

        // Set Type 1
        pokemonType1Display.text = pokemon.type1
        pokemonType1Display.visibility = View.VISIBLE
        // Example: if (pokemon.type1 == "Fire") pokemonType1Display.setBackgroundResource(R.drawable.rounded_type_badge_red)

        // Set Type 2 (if exists)
        if (pokemon.type2.isNotEmpty() && pokemon.type2 != "None") { // "None" is in your CSV
            pokemonType2Display.text = pokemon.type2.lowercase()
            pokemonType2Display.visibility = View.VISIBLE
            // Example: if (pokemon.type2 == "Flying") pokemonType2Display.setBackgroundResource(R.drawable.rounded_type_badge_blue)
        } else {
            pokemonType2Display.visibility = View.GONE
        }

        typeBadgesContainer.visibility = View.VISIBLE // Show the container if at least one type is present
//
//        // Set Base Stats
//        hpStatDisplay.text = pokemon.hp.toString()
//        attackStatDisplay.text = pokemon.attack.toString()
//        defenseStatDisplay.text = pokemon.defense.toString()
//        spAtkStatDisplay.text = pokemon.spAtk.toString()
//        spDefStatDisplay.text = pokemon.spDef.toString()
//        speedStatDisplay.text = pokemon.speed.toString()
//
        baseStatsTitle.visibility = View.VISIBLE
        pokemonStatsContainer.visibility = View.VISIBLE

        val infoText = """
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

}