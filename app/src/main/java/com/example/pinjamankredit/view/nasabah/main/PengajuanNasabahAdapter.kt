package com.example.pinjamankredit.view.nasabah.main

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.pinjamankredit.databinding.RowPengajuanBinding
import com.example.pinjamankredit.response.PengajuanResponse
import com.example.pinjamankredit.util.Helper

class PengajuanNasabahAdapter(
    var datas: ArrayList<PengajuanResponse.Data>,
    var listener: OnAdapterListener
) : RecyclerView.Adapter<PengajuanNasabahAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        RowPengajuanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    @RequiresApi(Build.VERSION_CODES.O)
    @Override
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = datas[position]
        var helper = Helper()

        when (model.status_pengajuan) {
            "Di tolak" -> {
                holder.binding.jumblahPengajuan.text = helper.formatRupiah(model.dana_pinjaman_diajukan)
            }
            "Di terima" -> {
                holder.binding.jumblahPengajuan.text = helper.formatRupiah(model.dana_pinjaman_diterima)
            }
            "konfirmasi" -> {
                holder.binding.jumblahPengajuan.text = helper.formatRupiah(model.dana_pinjaman_diajukan)
            }
        }
        holder.binding.tglPengajuan.text = helper.ubahFormatTanggal(model.tgl_pengajuan)
        holder.binding.lamaAnsuran.text = model.lama_ansuran

        holder.itemView.setOnClickListener {
            listener.onClick(model)
        }
    }


    override fun getItemCount() = datas.size

    class ViewHolder(val binding: RowPengajuanBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnAdapterListener {
        fun onClick(result: PengajuanResponse.Data)
    }

    fun setData(data: List<PengajuanResponse.Data>) {
        datas.clear()
        datas.addAll(data)
        notifyDataSetChanged()
    }

    fun filterDataByStatus(tabs: String) {
        val filteredList = ArrayList<PengajuanResponse.Data>()


        for (data in datas) {
            val status = data.status_pengajuan

            when (tabs) {
                "Di Tolak" -> {
                    if (status.equals("Di tolak")) {
                        filteredList.add(data)
                    }
                }
                "Di Terima" -> {
                    if (status.equals("Di terima")) {
                        filteredList.add(data)
                    }
                }
                "konfirmasi" -> {
                    if (status.equals("konfirmasi")) {
                        filteredList.add(data)
                    }
                }
            }
        }
        setData(filteredList)
    }

}