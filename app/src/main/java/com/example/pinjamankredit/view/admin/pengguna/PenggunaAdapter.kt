package com.example.pinjamankredit.view.admin.pengguna

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pinjamankredit.databinding.RowDataBinding
import com.example.pinjamankredit.response.NasabahResponse
import com.example.pinjamankredit.response.PenggunaResponse

class PenggunaAdapter(
    var datas: ArrayList<PenggunaResponse.Data>,
    var listener: OnAdapterListener
) : RecyclerView.Adapter<PenggunaAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        RowDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    @Override
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = datas[position]

        holder.binding.namaPeminjam.text = model.level
        holder.binding.namaOrganisasiPeminjam.text = model.nama_pengguna

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
        fun onClick(result: PenggunaResponse.Data)
        fun onClickShow(result: PenggunaResponse.Data)
        fun onClickEdit(result: PenggunaResponse.Data)
        fun onClickDelete(result: PenggunaResponse.Data)
    }

    fun setData(data: List<PenggunaResponse.Data>) {
        datas.clear()
        datas.addAll(data)
        notifyDataSetChanged()
    }

}