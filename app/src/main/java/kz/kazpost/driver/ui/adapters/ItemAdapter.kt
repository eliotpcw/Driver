package kz.kazpost.driver.ui.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_items.view.*
import kz.kazpost.driver.R
import kz.kazpost.driver.data.enums.ItemModel
import kz.kazpost.driver.utils.inflate

interface OnItemClickListener{
    fun onItemClicked(model: ItemModel)
}

class ItemAdapter(
    private val list: MutableList<ItemModel>,
    private val listener: OnItemClickListener?
): RecyclerView.Adapter<ItemAdapter.ItemsViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsViewHolder {
        val inflatedView = parent.inflate(R.layout.item_items, false)
        return ItemsViewHolder(inflatedView)
    }
    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {
        val model = list[position]
        holder.bind(model, listener)
    }
    override fun getItemCount(): Int = list.size

    inner class ItemsViewHolder(v: View): RecyclerView.ViewHolder(v)  {
        private var typeLabel: TextView = v.itemi_type
        private var shpiLabel: TextView = v.itemi_shpi
        private var fromLabel: TextView = v.itemi_from
        private var toLabel: TextView = v.itemi_to
        private var actLabel: TextView = v.itemi_act

        fun bind(model: ItemModel, listener: OnItemClickListener?) {
            typeLabel.text = model.typeName
            shpiLabel.text = model.shpi
            fromLabel.text = model.fromName
            toLabel.text = model.toName
            actLabel.text = if (model.hasAct) "Есть" else "Нет"

            itemView.setOnClickListener {
                //listener?.onItemClicked(model)
            }
        }
    }

}