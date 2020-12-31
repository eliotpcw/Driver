package kz.kazpost.driver.ui.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.accept_item.view.*
import kz.kazpost.driver.R
import kz.kazpost.driver.data.enums.LabelsModel
import kz.kazpost.driver.utils.inflate

class TransferInvoiceAdapter(
    private var list: MutableList<LabelsModel>
):
    RecyclerView.Adapter<TransferInvoiceAdapter.LabelsViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelsViewHolder {
        val inflatedView = parent.inflate(R.layout.accept_item, false)
        return LabelsViewHolder(inflatedView)
    }
    override fun onBindViewHolder(holder: LabelsViewHolder, position: Int) {
        val model = list[position]
        holder.bind(model)
    }
    override fun getItemCount(): Int = list.size

    inner class LabelsViewHolder(v: View): RecyclerView.ViewHolder(v)  {
        private var nameLabel: TextView = v.accept_item_name
        private var factLabel: TextView = v.accept_item_fact
        private var planLabel = v.accept_item_plan

        fun bind(model: LabelsModel) {
            nameLabel.text = model.name
            planLabel.text = model.plan.toString()
            factLabel.text = model.fact.toString()
        }
    }
}