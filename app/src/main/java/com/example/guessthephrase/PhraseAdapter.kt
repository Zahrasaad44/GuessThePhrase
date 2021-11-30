package com.example.guessthephrase

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycled_row.view.*

class PhraseAdapter(val context: Context, private val userGuess: ArrayList<String>):
    RecyclerView.Adapter<PhraseAdapter.GuessViewHolder>() {
    class GuessViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuessViewHolder {
        return  GuessViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.recycled_row, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: GuessViewHolder, position: Int) {
        var recycledText = userGuess[position]

        holder.itemView.apply {
            resultTV.text = recycledText

            if(recycledText.startsWith("Found")) {
                resultTV.setTextColor(Color.GREEN)
            } else if (recycledText.startsWith("No") || recycledText.startsWith("Wrong")) {
                resultTV.setTextColor(Color.RED)
            } else {
                resultTV.setTextColor(Color.BLACK)
            }
        }
    }

    override fun getItemCount() = userGuess.size

}
