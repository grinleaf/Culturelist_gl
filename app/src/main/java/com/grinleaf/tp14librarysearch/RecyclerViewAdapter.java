package com.grinleaf.tp14librarysearch;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.VH> {

    Context context;
    ArrayList<PerformDetailData> datas;

    public RecyclerViewAdapter(Context context, ArrayList<PerformDetailData> performDetailData) {
        this.context = context;
        this.datas = performDetailData;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.recycler_item,parent,false);

        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        PerformDetailData data= datas.get(position);

        String imgUrl= data.iv;
        Glide.with(((MainActivity)MainActivity.mainContext)).load(imgUrl).into(holder.iv);
//        Picasso.get().load(imgUrl).into(holder.iv);

        holder.title.setText(data.title);
        holder.genre.setText(data.genre);
        holder.author.setText(data.author);
        holder.product.setText(data.product);
        holder.state.setText(data.state);
        holder.dateStart.setText(data.dateStart);
        holder.dateEnd.setText(data.dateEnd);
        holder.cast.setText(data.cast);
        holder.place.setText(data.place);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class VH extends RecyclerView.ViewHolder{

        ImageView iv, tag;
        TextView title, genre, author, product, state, dateStart, dateEnd, cast, place;

        public VH(@NonNull View itemView) {
            super(itemView);
            iv= itemView.findViewById(R.id.item_iv);
            title= itemView.findViewById(R.id.item_tv_title);
            genre= itemView.findViewById(R.id.item_tv_genre);
            author= itemView.findViewById(R.id.item_tv_author);
            product= itemView.findViewById(R.id.item_tv_product);
            state= itemView.findViewById(R.id.item_tv_state);
            dateStart= itemView.findViewById(R.id.item_tv_date_start);
            dateEnd= itemView.findViewById(R.id.item_tv_date_end);
            cast= itemView.findViewById(R.id.item_tv_cast);
            place= itemView.findViewById(R.id.item_tv_place);
            tag= itemView.findViewById(R.id.item_iv_scrap);

            place.setOnClickListener(v -> {
                Intent intent= new Intent(MainActivity.mainContext,MapActivity.class);
                int pos= getAdapterPosition();  //클릭한 item 의 위치번호!
                intent.putExtra("hallId", datas.get(pos).hallID);
                MainActivity.mainContext.startActivity(intent);
            });

            tag.setOnClickListener(v->{
                Drawable star = ResourcesCompat.getDrawable(context.getResources(),R.drawable.ic_baseline_star_24 ,null);
                Drawable emptyStar = ResourcesCompat.getDrawable(context.getResources(),R.drawable.ic_baseline_star_border_24 ,null);
                Bitmap starBitmap = drawableToBitmap(star);
                Bitmap emptyStarBitmap = drawableToBitmap(emptyStar);
                Bitmap currentStarState = drawableToBitmap(tag.getDrawable());

                if(sameAs(starBitmap,currentStarState)) tag.setImageResource(R.drawable.ic_baseline_star_border_24);
                else if(sameAs(emptyStarBitmap,currentStarState)) tag.setImageResource(R.drawable.ic_baseline_star_24);
            });

        }

        //이미지비교 (1) : 이미지 --> 비트맵 변환 메소드
        public Bitmap drawableToBitmap (Drawable drawable) {
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable)drawable).getBitmap();
            }
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            return bitmap;
        }

        //이미지비교 (2) : 비트맵 비교 메소드
        private boolean sameAs(Bitmap bitmap1, Bitmap bitmap2) {

            ByteBuffer buffer1 = ByteBuffer.allocate(bitmap1.getHeight() * bitmap1.getRowBytes());
            bitmap1.copyPixelsToBuffer(buffer1);

            ByteBuffer buffer2 = ByteBuffer.allocate(bitmap2.getHeight() * bitmap2.getRowBytes());
            bitmap2.copyPixelsToBuffer(buffer2);
            return Arrays.equals(buffer1.array(), buffer2.array());

        }
    }
}

