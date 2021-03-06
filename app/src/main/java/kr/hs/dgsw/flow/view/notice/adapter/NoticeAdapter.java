package kr.hs.dgsw.flow.view.notice.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.hs.dgsw.flow.R;
import kr.hs.dgsw.flow.util.FlowUtils;
import kr.hs.dgsw.flow.view.notice.listener.OnItemClickListener;
import kr.hs.dgsw.flow.util.retrofit.model.notice.ResponseNoticeItem;

public class NoticeAdapter
        extends RecyclerView.Adapter<NoticeAdapter.ViewHolder>
        implements INoticeAdapterContract.View, INoticeAdapterContract.Model{

    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    private ArrayList<ResponseNoticeItem> mNoticeList = new ArrayList<>();

    public NoticeAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(mContext)
                .inflate(R.layout.item_notice, parent, false);
        return new ViewHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ResponseNoticeItem noticeItem = mNoticeList.get(position);

        holder.mWriter.setText(noticeItem.getWriter());

        try {
            String writeDate = FlowUtils.dateFormat(noticeItem.getWriteDate());
            holder.mWriteDate.setText(writeDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String content = noticeItem.getContent();
        // 20자 까지만 보여줌
        if (content.length() > 20) {
            content = content.substring(0, 19);
            content = content + "...";
        }
        holder.mContent.setText(content);

        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return mNoticeList.size();
    }

    @Override
    public void notifyAdapter() {
        notifyItemChanged(0, mNoticeList.size());
    }

    @Override
    public void setOnClickListener(OnItemClickListener clickListener) {
        mOnItemClickListener = clickListener;
    }

    @Override
    public void addItems(ArrayList<ResponseNoticeItem> noticeList) {
        mNoticeList.addAll(noticeList);
    }

    @Override
    public void clearItems() {
        mNoticeList.clear();
    }

    @Override
    public ResponseNoticeItem getItem(int position) {
        return mNoticeList.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.notice_writer)
        public TextView mWriter;

        @BindView(R.id.notice_write_date)
        public TextView mWriteDate;

        @BindView(R.id.notice_content)
        public TextView mContent;

        public View mItemView;

        private OnItemClickListener mOnItemClickListener;

        public ViewHolder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            mItemView = itemView;
            mOnItemClickListener = onItemClickListener;
            ButterKnife.bind(this, itemView);
        }

        public void onBind(int position) {
            mItemView.setOnClickListener((v) -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(position);
                }
            });
        }
    }
}
