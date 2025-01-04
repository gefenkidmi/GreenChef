package com.example.greenchef.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.greenchef.R

class IngredientAdapter(
    private val originalList: List<String>
) : RecyclerView.Adapter<IngredientAdapter.ViewHolder>(), Filterable {

    private var filteredList: List<String> = originalList.toMutableList()
    private var checkedItems: MutableSet<String> = mutableSetOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ingredient_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ingredient = filteredList[position]
        holder.bind(ingredient, checkedItems.contains(ingredient))
    }

    override fun getItemCount(): Int = filteredList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkboxIngredient: CheckBox = itemView.findViewById(R.id.checkboxIngredient)

        fun bind(ingredient: String, isChecked: Boolean) {
            checkboxIngredient.text = ingredient
            checkboxIngredient.isChecked = isChecked

            checkboxIngredient.setOnClickListener {
                if (checkboxIngredient.isChecked) {
                    checkedItems.add(ingredient)
                } else {
                    checkedItems.remove(ingredient)
                }
            }
        }

    }

    // Method to get the list of checked items
    fun getCheckedItems(): Set<String> {
        return checkedItems
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                val query = constraint?.toString()?.toLowerCase()

                val filteredList = if (query.isNullOrBlank()) {
                    originalList
                } else {
                    originalList.filter { it.toLowerCase().contains(query) }
                }

                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                @Suppress("UNCHECKED_CAST")
                filteredList = results?.values as? List<String> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }
}



