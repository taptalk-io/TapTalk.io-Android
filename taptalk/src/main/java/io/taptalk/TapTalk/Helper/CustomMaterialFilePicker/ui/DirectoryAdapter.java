package io.taptalk.TapTalk.Helper.CustomMaterialFilePicker.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.List;

import io.taptalk.TapTalk.Helper.CustomMaterialFilePicker.utils.FileTypeUtils;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.Taptalk.R;

/**
 * Created by Dimorinny on 24.10.15.
 */

public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.DirectoryViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public class DirectoryViewHolder extends RecyclerView.ViewHolder {
        private ImageView mFileImage;
        private TextView mFileTitle;
        private TextView mFileSubtitle;

        public DirectoryViewHolder(View itemView, final OnItemClickListener clickListener) {
            super(itemView);

            itemView.setOnClickListener(v -> clickListener.onItemClick(v, getAdapterPosition()));

            mFileImage = itemView.findViewById(R.id.item_file_image);
            mFileTitle = itemView.findViewById(R.id.item_file_title);
            mFileSubtitle = itemView.findViewById(R.id.item_file_subtitle);
        }
    }

    private List<File> mFiles;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public DirectoryAdapter(Context context, List<File> files) {
        mContext = context;
        mFiles = files;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    @Override
    public DirectoryViewHolder onCreateViewHolder(ViewGroup parent,
                                                  int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tap_item_file, parent, false);

        return new DirectoryViewHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(DirectoryViewHolder holder, int position) {
        File currentFile = mFiles.get(position);

        FileTypeUtils.FileType fileType = FileTypeUtils.getFileType(currentFile);
        if (FileTypeUtils.FileType.IMAGE != fileType && FileTypeUtils.FileType.VIDEO != fileType) {
            holder.mFileImage.setImageResource(fileType.getIcon());
            holder.mFileImage.setAlpha(0.6f);
        } else {
            Glide.with(holder.itemView.getContext()).load(currentFile).apply(new RequestOptions().centerCrop()).into(holder.mFileImage);
            holder.mFileImage.setAlpha(1.0f);
        }
        if (FileTypeUtils.FileType.DIRECTORY != fileType) {
            String stringBld = TAPUtils.getInstance().getStringSizeLengthFile(currentFile.length()) +
                    " - " +
                    fileType;
            holder.mFileSubtitle.setText(stringBld);
        } else holder.mFileSubtitle.setText(fileType.getDescription());
        holder.mFileTitle.setText(currentFile.getName());
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public File getModel(int index) {
        return mFiles.get(index);
    }
}