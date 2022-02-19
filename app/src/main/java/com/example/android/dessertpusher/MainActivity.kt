/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.dessertpusher

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.example.android.dessertpusher.databinding.ActivityMainBinding
import timber.log.Timber

const val REVENUE_KEY = "REVENUE"

class MainActivity : AppCompatActivity(), LifecycleObserver {
    private var revenue = 0
    private var dessertsSold = 0
    private lateinit var dessertTimer: DessertTimer

    // Contains all the views
    private lateinit var binding: ActivityMainBinding

    /** Dessert Data **/

    /**
     * Simple data class that represents a dessert. Includes the resource id integer associated with
     * the image, the price it's sold for, and the startProductionAmount, which determines when
     * the dessert starts to be produced.
     */
    data class Dessert(val imageId: Int, val price: Int, val startProductionAmount: Int)

    // Create a list of all desserts, in order of when they start being produced
    private val allDesserts = listOf(
        Dessert(R.drawable.cupcake, 5, 0),
        Dessert(R.drawable.donut, 10, 2),
        Dessert(R.drawable.eclair, 15, 5),
        Dessert(R.drawable.froyo, 30, 20),
        Dessert(R.drawable.gingerbread, 50, 30),
        Dessert(R.drawable.honeycomb, 100, 40),
        Dessert(R.drawable.icecreamsandwich, 500, 50),
        Dessert(R.drawable.jellybean, 1000, 60),
        Dessert(R.drawable.kitkat, 2000, 70),
        Dessert(R.drawable.lollipop, 3000, 80),
        Dessert(R.drawable.marshmallow, 4000, 90),
        Dessert(R.drawable.nougat, 5000, 100),
        Dessert(R.drawable.oreo, 6000, 200)
    )
    private var currentDessert = allDesserts[0]

    @SuppressLint("LogNotTimber")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.i("onCreate() called")
        dessertTimer = DessertTimer()
        lifecycle.addObserver(dessertTimer)

        // Use Data Binding to get reference to the views
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.dessertButton.setOnClickListener {
            onDessertClicked()
        }


        savedInstanceState?.let{ bundle ->
            revenue = bundle.getInt(REVENUE_KEY, 0)
        }


        // Set the TextViews to the right values
        binding.revenue = revenue
        binding.amountSold = dessertsSold

        // Make sure the correct dessert is showing
        binding.dessertButton.setImageResource(currentDessert.imageId)
    }

    override fun onStart() {
        super.onStart()
        Timber.i("onStart() called")
    }

    override fun onPause() {
        super.onPause()
        Timber.i("onPause() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("onDestroy() called")
    }

    override fun onRestart() {
        super.onRestart()
        Timber.i("onRestart() called")
    }

    override fun onResume() {
        super.onResume()
        Timber.i("onResume() called")
    }

    override fun onStop() {
        super.onStop()
        Timber.i("onStop() called")
    }

    /**
     * Updates the score when the dessert is clicked. Possibly shows a new dessert.
     */
    private fun onDessertClicked() {

        // Update the score
        revenue += currentDessert.price
        dessertsSold++

        binding.revenue = revenue
        binding.amountSold = dessertsSold

        // Show the next dessert
        showCurrentDessert()
    }

    /**
     * Determine which dessert to show.
     */
    private fun showCurrentDessert() {
        var newDessert = allDesserts[0]
        for (dessert in allDesserts) {
            if (dessertsSold >= dessert.startProductionAmount) {
                newDessert = dessert
            }
            // The list of desserts is sorted by startProductionAmount. As you sell more desserts,
            // you'll start producing more expensive desserts as determined by startProductionAmount
            // We know to break as soon as we see a dessert who's "startProductionAmount" is greater
            // than the amount sold.
            else break
        }

        // If the new dessert is actually different than the current dessert, update the image
        if (newDessert != currentDessert) {
            currentDessert = newDessert
            binding.dessertButton.setImageResource(newDessert.imageId)
        }
    }

    /**
     * Menu methods
     */
    private fun onShare() {
        val shareIntent = ShareCompat.IntentBuilder.from(this)
            .setText(getString(R.string.share_text, dessertsSold, revenue))
            .setType("text/plain")
            .intent
        try {
            startActivity(shareIntent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                this, getString(R.string.sharing_not_available),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.shareMenuButton -> onShare()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(REVENUE_KEY, revenue)
        Timber.i("saveInstanceState")
        super.onSaveInstanceState(outState)
    }
}
