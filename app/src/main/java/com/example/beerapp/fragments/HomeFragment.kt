package com.example.beerapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beerapp.R
import com.example.beerapp.ViewModel.CartViewModel
import com.example.beerapp.activities.adapter.Item
import com.example.beerapp.activities.adapter.ItemAdapter

class HomeFragment : Fragment() {

    private lateinit var adapter1: ItemAdapter
    private lateinit var adapter2: ItemAdapter
    lateinit var viewModel: CartViewModel
    private val allItems = mutableListOf<Item>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(CartViewModel::class.java)

        val recyclerView1: RecyclerView = view.findViewById(R.id.recyclerView1)
        val recyclerView2: RecyclerView = view.findViewById(R.id.recyclerView2)

        recyclerView1.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView2.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        adapter1 = ItemAdapter(mutableListOf()) { item -> addToCart(item) }
        adapter2 = ItemAdapter(mutableListOf()) { item -> addToCart(item) }

        recyclerView1.adapter = adapter1
        recyclerView2.adapter = adapter2

        populateInitialData()
    }

    private fun populateInitialData() {
        val items = listOf(
            Item("Gorkha Beer 200ml", R.drawable.ic_beer, "Rs350/-"),
            Item("Mustang Beer 200ml", R.drawable.ic_beer, "Rs350/-"),
            Item("Corona Beer 200ml", R.drawable.ic_beer, "Rs350/-"),
            Item("Nepal Ice 200ml", R.drawable.ic_beer, "Rs350/-"),
            Item("Tuborg 200ml", R.drawable.ic_beer, "Rs350/-"),
            Item("Mustang Beer 200ml", R.drawable.ic_beer, "Rs350/-"),
        )

        for ((index, item) in items.withIndex()) {
            allItems.add(item)
        }

        updateRecyclerViews()
    }

    private fun updateRecyclerViews() {
        val itemsForRecyclerView1 = mutableListOf<Item>()
        val itemsForRecyclerView2 = mutableListOf<Item>()

        for ((index, item) in allItems.withIndex()) {
            if (index % 2 == 0) {
                itemsForRecyclerView1.add(item)
            } else {
                itemsForRecyclerView2.add(item)
            }
        }

        adapter1.updateData(itemsForRecyclerView1)
        adapter2.updateData(itemsForRecyclerView2)
    }

    private fun addToCart(item: Item) {
        viewModel.addItemToCart(item)
        Toast.makeText(requireContext(), "${item.name} added to cart", Toast.LENGTH_SHORT).show()
    }
}
