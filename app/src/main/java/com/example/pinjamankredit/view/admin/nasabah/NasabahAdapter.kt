package com.example.pinjamankredit.view.admin.nasabah

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pinjamankredit.databinding.RowDataBinding
import com.example.pinjamankredit.response.NasabahResponse
import com.example.pinjamankredit.response.PenggunaResponse

class NasabahAdapter(
    var datas: ArrayList<NasabahResponse.Data>,
    var listener: OnAdapterListener

) : RecyclerView.Adapter<NasabahAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        RowDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    @Override
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = datas[position]

        holder.binding.namaPeminjam.text = model.email
        holder.binding.namaOrganisasiPeminjam.text = model.nama_nasabah

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
        fun onClick(result: NasabahResponse.Data)
        fun onClickShow(result: NasabahResponse.Data)
        fun onClickEdit(result: NasabahResponse.Data)
        fun onClickDelete(result: NasabahResponse.Data)
    }

    fun setData(data: List<NasabahResponse.Data>) {
        datas.clear()
        datas.addAll(data)
        notifyDataSetChanged()
    }

}