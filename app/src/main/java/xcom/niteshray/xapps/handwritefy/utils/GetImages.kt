package xcom.niteshray.xapps.handwritefy.utils

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore

class GetImages {

    companion object{
        fun loadSavedImages(context: Context): List<Uri> {
            val imageList = mutableListOf<Uri>()
            val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.RELATIVE_PATH
            )

            val selection = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
            val selectionArgs = arrayOf("%Pictures/Handwritefy/%")

            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

            context.contentResolver.query(
                collection,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val contentUri = ContentUris.withAppendedId(collection, id)
                    imageList.add(contentUri)
                }
            }

            return imageList
        }

    }
}