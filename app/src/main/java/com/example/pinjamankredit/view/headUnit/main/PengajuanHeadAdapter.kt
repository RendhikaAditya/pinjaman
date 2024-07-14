package com.example.pinjamankredit.view.headUnit.main
import com.example.pinjamankredit.util.Helper


import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.pinjamankredit.databinding.RowPersetujuanBinding
import PengajuanResponse
class PengajuanHeadAdapter(
    var datas: ArrayList<PengajuanResponse.Data>,
    var listener: OnAdapterListener
) : RecyclerView.Adapter<PengajuanHeadAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        RowPersetujuanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    @RequiresApi(Build.VERSION_CODES.O)
    @Override
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = datas[position]
        var helper = Helper()

        when (model.status_pengajuan) {
            "Di tolak" -> {
                holder.binding.btnSetujui.visibility = View.GONE
                holder.binding.btnTolak.visibility = View.GONE
                holder.binding.jumblahPengajuan.text = helper.formatRupiah(model.dana_pinjaman_diajukan)
            }
            "Di terima" -> {
                holder.binding.btnSetujui.visibility = View.GONE
                holder.binding.btnTolak.visibility = View.GONE
                holder.binding.jumblahPengajuan.text = helper.formatRupiah(model.dana_pinjaman_diterima)
            }
            "konfirmasi" -> {
                holder.binding.btnSetujui.visibility = View.VISIBLE
                holder.binding.btnTolak.visibility = View.VISIBLE

                holder.binding.jumblahPengajuan.text = helper.formatRupiah(model.dana_pinjaman_diajukan)
            }
        }
        holder.binding.tglPengajuan.text = helper.ubahFormatTanggal(model.tgl_pengajuan)
        holder.binding.lamaAnsuran.text = model.lama_ansuran
        holder.binding.txtKeterangan.text = model.keterangan
        holder.itemView.setOnClickListener {
            listener.onClick(model)
        }
        holder.binding.btnSetujui.setOnClickListener {
            listener.onClickSetujui(model)
        }
        holder.binding.btnTolak.setOnClickListener {
            listener.onClickTolak(model)
        }
    }


    override fun getItemCount() = datas.size

    class ViewHolder(val binding: RowPersetujuanBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnAdapterListener {
        fun onClick(result: PengajuanResponse.Data)
        fun onClickSetujui(model: PengajuanResponse.Data)
        fun onClickTolak(model: PengajuanResponse.Data)
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