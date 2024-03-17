package com.example.pinjamankredit.view.admin.pengajuan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pinjamankredit.databinding.RowDataBinding
import com.example.pinjamankredit.response.NasabahResponse
import com.example.pinjamankredit.response.PengajuanResponse
import com.example.pinjamankredit.util.Helper

class PengajuanAdapter(
    var datas: ArrayList<PengajuanResponse.Data>,
    var listener: OnAdapterListener
) : RecyclerView.Adapter<PengajuanAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        RowDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    @Override
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = datas[position]
        var helper = Helper()

        holder.binding.namaPeminjam.text = model.nasabah.nama_nasabah
        holder.binding.namaOrganisasiPeminjam.text = "Diajukan : "+helper.formatRupiah(model.dana_pinjaman_diajukan)+"\nDiterima  : "+helper.formatRupiah(model.dana_pinjaman_diterima)

        holder.binding.btnEdit.visibility = View.GONE
        holder.binding.btnHapus.visibility = View.GONE
        holder.itemView.setOnClickListener {
            listener.onClick(model)
        }
        holder.binding.btnLihatData.setOnClickListener {
            listener.onClickShow(model)
        }

        holder.binding.btnHapus.setOnClickListener {
            listener.onClickDelete(model)
        }

        holder.binding.btnEdit.setOnClickListener {
            listener.onClickEdit(model)
        }
    }

    override fun getItemCount() = datas.size

    class ViewHolder(val binding: RowDataBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnAdapterListener {
        fun onClick(result: PengajuanResponse.Data)
        fun onClickShow(result: PengajuanResponse.Data)
        fun onClickEdit(result: PengajuanResponse.Data)
        fun onClickDelete(result: PengajuanResponse.Data)
    }

    fun setData(data: List<PengajuanResponse.Data>) {
        datas.clear()
        datas.addAll(data)
        notifyDataSetChanged()
    }

}