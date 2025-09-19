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
    private lateinit var pokemonNameInput: EditText
    private lateinit var searchButton: Button

    private lateinit var pokemonNameDisplay: TextView
    private lateinit var pokemonType1Display: TextView
    private lateinit var pokemonType2Display: TextView
    private lateinit var typeBadgesContainer: LinearLayout
    private lateinit var totalStats: TextView
    private lateinit var pokemonStatsContainer: LinearLayout
    private lateinit var hpStatDisplay: TextView
    private lateinit var attackStatDisplay: TextView
    private lateinit var defenseStatDisplay: TextView
    private lateinit var spAtkStatDisplay: TextView
    private lateinit var spDefStatDisplay: TextView
    private lateinit var speedStatDisplay: TextView
    private lateinit var generationDisplay: TextView
    private lateinit var legendaryDisplay: TextView

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
        pokemonNameInput = findViewById(R.id.pokemonNameInput)
        searchButton = findViewById(R.id.searchButton)

        pokemonNameDisplay = findViewById(R.id.pokemonNameDisplay)
        pokemonType1Display = findViewById(R.id.pokemonType1Display)
        pokemonType2Display = findViewById(R.id.pokemonType2Display)
        typeBadgesContainer = findViewById(R.id.typeBadgesContainer)
        totalStats = findViewById(R.id.TotalStats)
        pokemonStatsContainer = findViewById(R.id.pokemonStatsContainer)
        hpStatDisplay = findViewById(R.id.hpStatDisplay)
        attackStatDisplay = findViewById(R.id.attackStatDisplay)
        defenseStatDisplay = findViewById(R.id.defenseStatDisplay)
        spAtkStatDisplay = findViewById(R.id.spAtkStatDisplay)
        spDefStatDisplay = findViewById(R.id.spDefStatDisplay)
        speedStatDisplay = findViewById(R.id.speedStatDisplay)
        generationDisplay = findViewById(R.id.GenerationDisplay)
        legendaryDisplay = findViewById(R.id.LegendaryDisplay)

        searchButton.setOnClickListener {
            performSearch()
        }

        pokemonNameInput.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH || keyEvent?.keyCode == android.view.KeyEvent.KEYCODE_ENTER) {
                performSearch()
                // Return true to indicate that the event has been handled
                true
            } else {
                false
            }
        }

        hidePokemonDisplayElements()

        pokemonNameDisplay.text = getString(R.string.welcome)
        pokemonNameDisplay.visibility = View.VISIBLE

        totalStats.text = getString(R.string.hint)
        totalStats.visibility = View.VISIBLE
    }

    private fun performSearch() {
        val nameToSearch = pokemonNameInput.text.toString()
        val foundPokemon = findPokemonByName(nameToSearch)

        if (foundPokemon != null) {
            displayPokemonInfo(foundPokemon)
            showPokemonDisplayElements()
        } else {
            pokemonNameDisplay.text = getString(R.string.missing)
            pokemonNameDisplay.visibility = View.VISIBLE // Show general error message
            pokemonImageView.setImageResource(R.drawable.pokeball)
            hidePokemonDisplayElements() // Hide specific details
        }
    }

    private fun findPokemonByName(name: String): Pokedex? {
        return pokemonList.find { it.name.equals(name, ignoreCase = true) }
    }

    private fun hidePokemonDisplayElements() {
        pokemonType1Display.visibility = View.INVISIBLE
        pokemonType2Display.visibility = View.INVISIBLE
        totalStats.visibility = View.INVISIBLE
        pokemonStatsContainer.visibility = View.INVISIBLE
    }

    private fun showPokemonDisplayElements() {
        pokemonNameDisplay.visibility = View.VISIBLE
        pokemonType1Display.visibility = View.VISIBLE
        typeBadgesContainer.visibility = View.VISIBLE
        totalStats.visibility = View.VISIBLE
        pokemonStatsContainer.visibility = View.VISIBLE
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
            pokemonImageView.setImageResource(R.drawable.pokeball)
        }

        // Set Pokemon Name
        pokemonNameDisplay.text = pokemon.name

        // Set Type 1
        pokemonType1Display.text = pokemon.type1
        pokemonType1Display.setBackgroundResource(getTypeBadgeResource(pokemon.type1))

        // Set Type 2 (if exists)
        if (pokemon.type2.isNotEmpty() && pokemon.type2 != "None") { // "None" is in your CSV
            pokemonType2Display.text = pokemon.type2.lowercase()
            pokemonType2Display.setBackgroundResource(getTypeBadgeResource(pokemon.type2))
            pokemonType2Display.visibility = View.VISIBLE
        } else {
            pokemonType2Display.visibility = View.GONE
        }

        // Set Stats
        totalStats.text = getString(R.string.Total, pokemon.total)
        hpStatDisplay.text = pokemon.hp.toString()
        attackStatDisplay.text = pokemon.attack.toString()
        defenseStatDisplay.text = pokemon.defense.toString()
        spAtkStatDisplay.text = pokemon.spAtk.toString()
        spDefStatDisplay.text = pokemon.spDef.toString()
        speedStatDisplay.text = pokemon.speed.toString()
        generationDisplay.text = pokemon.generation.toString()
        legendaryDisplay.text = if (pokemon.isLegendary) "Yes" else "No"

    }

    private fun getTypeBadgeResource(type: String): Int {
        return when (type.lowercase()) {
            "grass" -> R.drawable.rounded_type_badge_grass
            "fire" -> R.drawable.rounded_type_badge_fire
            "water" -> R.drawable.rounded_type_badge_water
            "bug" -> R.drawable.rounded_type_badge_bug
            "normal" -> R.drawable.rounded_type_badge_normal
            "poison" -> R.drawable.rounded_type_badge_poison
            "electric" -> R.drawable.rounded_type_badge_electric
            "ground" -> R.drawable.rounded_type_badge_ground
            "fairy" -> R.drawable.rounded_type_badge_fairy
            "fighting" -> R.drawable.rounded_type_badge_fighting
            "psychic" -> R.drawable.rounded_type_badge_psychic
            "rock" -> R.drawable.rounded_type_badge_rock
            "ghost" -> R.drawable.rounded_type_badge_ghost
            "ice" -> R.drawable.rounded_type_badge_ice
            "dragon" -> R.drawable.rounded_type_badge_dragon
            "dark" -> R.drawable.rounded_type_badge_dark
            "steel" -> R.drawable.rounded_type_badge_steel
            "flying" -> R.drawable.rounded_type_badge_flying
            else -> R.drawable.rounded_type_badge_water
        }
    }

}