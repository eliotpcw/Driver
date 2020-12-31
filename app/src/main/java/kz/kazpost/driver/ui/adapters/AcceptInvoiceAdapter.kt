package kz.kazpost.driver.ui.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_labels.view.*
import kz.kazpost.driver.R
import kz.kazpost.driver.data.models.LabelDataListItem
import kz.kazpost.driver.data.models.MailItem
import kz.kazpost.driver.utils.inflate

interface AcceptInvoiceAdapterInterface {
    fun onFactChanged(sum: Int, isSavable: Boolean)
}

class AcceptInvoiceAdapter: RecyclerView.Adapter<AcceptInvoiceAdapter.LabelsViewHolder>()  {

    private var planList: List<LabelDataListItem?> = mutableListOf()
//    private var factList: List<LabelDataListItem?> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelsViewHolder {
        val inflatedView = parent.inflate(R.layout.item_labels, false)
        return LabelsViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: LabelsViewHolder, position: Int) {
        val model = planList[position]
        model?.let { holder.bindLabel(it) }
    }
    override fun getItemCount(): Int = planList.size

    fun setLabelDataList(newList: MutableList<LabelDataListItem>){
        planList = newList
        notifyDataSetChanged()
    }

    fun getLabelDataList(): MutableList<LabelDataListItem?> {
        return planList.toMutableList()
    }

    inner class LabelsViewHolder(v: View): RecyclerView.ViewHolder(v)  {
        private var nameLabel: TextView = v.iteml_name
        private var planLabel: TextView = v.iteml_plan

        fun bindLabel(model: LabelDataListItem) {
            nameLabel.text = model.name
            planLabel.text = model.count.toString()
        }
    }
}