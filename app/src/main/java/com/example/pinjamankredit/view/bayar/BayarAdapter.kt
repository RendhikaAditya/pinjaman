package com.example.pinjamankredit.view.bayar

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.pinjamankredit.databinding.RowBayarBinding
import com.example.pinjamankredit.databinding.RowPersetujuanBinding
import com.example.pinjamankredit.response.BayarResponse
import com.example.pinjamankredit.response.PengajuanResponse
import com.example.pinjamankredit.util.Constants
import com.example.pinjamankredit.util.Helper
import com.example.pinjamankredit.util.SharedPreferences

class BayarAdapter(
    var datas: ArrayList<BayarResponse.Data>,
    var listener: OnAdapterListener
) : RecyclerView.Adapter<BayarAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        RowBayarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    @RequiresApi(Build.VERSION_CODES.O)
    @Override
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = datas[position]
        var helper = Helper()
        var sharedPreferences = SharedPreferences(holder.itemView.context)

        if (sharedPreferences.getString(Constants.KEY_LEVEL).equals("Unit Head")){
            holder.binding.layoutBtnHead.visibility = View.VISIBLE
            holder.binding.layoutBtnNasabah.visibility = View.GONE
            when (model.status) {
                "Belum" -> {
                    holder.binding.icDone.visibility = View.GONE
                    holder.binding.btnSetujui.visibility = View.GONE
                }
                "Bayar" -> {
                    holder.binding.btnSetujui.visibility = View.VISIBLE
                    holder.binding.icDone.visibility = View.GONE
                }
                "Diterima" -> {
                    holder.binding.layoutBtnHead.visibility = View.GONE
                    holder.binding.icDone.visibility = View.VISIBLE
                }
            }
        }else{
            holder.binding.layoutBtnHead.visibility = View.GONE
            holder.binding.layoutBtnNasabah.visibility = View.VISIBLE
            when (model.status) {
                "Belum" -> {
                    holder.binding.btnBayar.visibility = View.VISIBLE
                    holder.binding.icDone.visibility = View.GONE
                }
                "Bayar" -> {
                    holder.binding.btnBayar.visibility = View.GONE
                    holder.binding.icDone.visibility = View.GONE
                }
                "Diterima" -> {
                    holder.binding.btnBayar.visibility = View.GONE
                    holder.binding.icDone.visibility = View.VISIBLE
                }
            }
        }
        holder.binding.jumblahAnsuran.text = model.nominal_bayaran
        holder.binding.ansuranKe.text = "Ansuran "+model.bulan_pembayaran

        holder.itemView.setOnClickListener {
            listener.onClick(model)
        }
        holder.binding.btnSetujui.setOnClickListener {
            listener.onClickSetujui(model)
        }
        holder.binding.btnBayar.setOnClickListener {
            listener.onClickBayar(model)
        }
    }


    override fun getItemCount() = datas.size

    class ViewHolder(val binding: RowBayarBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnAdapterListener {
        fun onClick(result: BayarResponse.Data)
        fun onClickSetujui(model: BayarResponse.Data)
        fun onClickBayar(model: BayarResponse.Data)
    }

    fun setData(data: List<BayarResponse.Data>) {
        datas.clear()
        datas.addAll(data)
        notifyDataSetChanged()
    }

//    fun filterDataByStatus(tabs: String) {
//        val filteredList = ArrayList<PengajuanResponse.Data>()
//
//
//        for (data in datas) {
//            val status = data.status_pengajuan
//
//            when (tabs) {
//                "Di Tolak" -> {
//                    if (status.equals("Di tolak")) {
//                        filteredList.add(data)
//                    }
//                }
//                "Di Terima" -> {
//                    if (status.equals("Di terima")) {
//                        filteredList.add(data)
//                    }
//                }
//                "konfirmasi" -> {
//                    if (status.equals("konfirmasi")) {
//                        filteredList.add(data)
//                    }
//                }
//            }
//        }
//        setData(filteredList)
//    }

}