package com.tracki.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tracki.R
import com.tracki.data.model.DocType
import com.tracki.data.model.DocumentType
import kotlinx.android.synthetic.main.item_menu.view.*

class GridMenuAdapter : RecyclerView.Adapter<GridMenuAdapter.MenuViewHolder>() {
    var listener: GridMenuListener? = null


    fun populateMenu(): ArrayList<DocumentType>? {
        var list = ArrayList<DocumentType>()
        for (i in 0..4) {
            var doc = DocumentType()
            if (i == 0) {
                doc.type = DocType.DOC
                doc.icon = R.drawable.ic_documents_doc
            } else if (i == 1) {
                doc.type = DocType.CAMERA
                doc.icon = R.drawable.ic_camera_doc
            } else if (i == 2) {
                doc.type = DocType.GALLERY
                doc.icon = R.drawable.ic_picture_doc
            } else if (i == 3) {
                doc.type = DocType.AUDIO
                doc.icon = R.drawable.ic_headphone
            } else if (i == 4) {
                doc.type = DocType.VIDEO
                doc.icon = R.drawable.ic_video_doc
            }/*else if (i == 5) {
                doc.type = DocType.PDF
                doc.icon = R.drawable.ic_pdf_doc
            }*/ else if (i == 5) {
                doc.type = DocType.PAYMENT
                doc.icon = R.drawable.ic_money
            }
            list.add(doc)
        }
        return list
    }
    interface GridMenuListener {
        fun dismissPopup()
        fun onItemClick(doc: DocumentType)
    }

    private val data = populateMenu()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        return MenuViewHolder.create(
                parent,
                viewType
        )
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(data?.get(position)!!, listener)
    }

    class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(
                menu: DocumentType,
                listener: GridMenuListener?
        ) {
            with(itemView) {

                tvTitle.text = menu.type!!.type
                menu.icon?.let { ivIcon.setImageResource(it) }
                itemView.setOnClickListener {

                    listener?.dismissPopup()
                    listener!!.onItemClick(menu)
                }
            }
        }

        companion object {
            val LAYOUT = R.layout.item_menu

            fun create(parent: ViewGroup, viewType: Int): MenuViewHolder {
                return MenuViewHolder(
                        LayoutInflater.from(parent.context).inflate(
                                LAYOUT,
                                parent,
                                false
                        )
                )
            }
        }
    }


}