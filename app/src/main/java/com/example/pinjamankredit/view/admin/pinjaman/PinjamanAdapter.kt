package com.example.pinjamankredit.view.admin.pinjaman

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pinjamankredit.databinding.RowDataBinding
import com.example.pinjamankredit.response.NasabahResponse
import PengajuanResponse
import com.example.pinjamankredit.response.PinjamanResponse
import com.example.pinjamankredit.util.Helper

class PinjamanAdapter(
    var datas: ArrayList<PinjamanResponse.Data>,
    var listener: OnAdapterListener
) : RecyclerView.Adapter<PinjamanAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        RowDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    @Override
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = datas[position]
        var helper = Helper()

        holder.binding.namaPeminjam.text = model.nasabah.nama_nasabah
        holder.binding.namaOrganisasiPeminjam.text = helper.formatRupiah(model.dana_pinjaman)+"\nAnsuran : "+model.lama_ansuran

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
        fun onClick(result: PinjamanResponse.Data)
        fun onClickShow(result: PinjamanResponse.Data)
        fun onClickEdit(result: PinjamanResponse.Data)
        fun onClickDelete(result: PinjamanResponse.Data)
    }

    fun setData(data: List<PinjamanResponse.Data>) {
        datas.clear()
        datas.addAll(data)
        notifyDataSetChanged()
    }

}