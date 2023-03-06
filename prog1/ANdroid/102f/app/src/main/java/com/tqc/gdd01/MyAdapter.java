package com.tqc.gdd01;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<String> data1;
        private List<String> data2;
        private List<String> data3;

        public MyAdapter(ArrayList<String> data1, ArrayList<String> data2, ArrayList<String> data3) {
            this.data1 = data1;
            this.data2 = data2;
            this.data3 = data3;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // 創建ViewHolder，並將自定義的項目布局加載進來
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.addlist, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // 綁定數據到ViewHolder
            holder.title.setText(data1.get(position));
            holder.body.setText(data2.get(position));
            holder.tail.setText(data3.get(position));
        }

        @Override
        public int getItemCount() {
            return data1.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView title,body,tail;

            public ViewHolder(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.textView11);
                body = itemView.findViewById(R.id.textView12);
                tail = itemView.findViewById(R.id.textView13);
            }
        }
    }

