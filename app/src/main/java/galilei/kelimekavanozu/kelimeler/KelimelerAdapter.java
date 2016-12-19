package galilei.kelimekavanozu.kelimeler;

/**
 * Created by magpi on 12/13/16.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import galilei.kelimekavanozu.R;

public class KelimelerAdapter extends RecyclerView.Adapter<KelimelerAdapter.NoteVH> {
    Context context;
    List<Note> notes;

    OnItemClickListener clickListener;

    public KelimelerAdapter(Context context, List<Note> notes) {
        this.context = context;
        this.notes = notes;

    }


    @Override
    public NoteVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        NoteVH viewHolder = new NoteVH(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final NoteVH holder, final int position) {

        holder.title.setText(notes.get(position).getTitle());
        holder.note.setText(notes.get(position).getNote());
        holder.time.setText(getDateFormat(notes.get(position).getTime()));
        holder.sharebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
//                sharingIntent.setType("text/plain");
                String shareBody = notes.get(position).getTitle().trim() + " " + notes.get(position).getNote().trim() + " #KelimeKavanozu";
//                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "#KelimeKavanozu");
//                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
//                context.startActivity(Intent.createChooser(sharingIntent, "Paylaş"));
                TweetComposer.Builder builder = new TweetComposer.Builder(context)
                        .text(shareBody);
                builder.show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    class NoteVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, note, time;
        ImageButton sharebutton;
        public NoteVH(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.note_item_title);
            note = (TextView) itemView.findViewById(R.id.note_item_desc);
            time = (TextView) itemView.findViewById(R.id.note_item_time);
                    itemView.setOnClickListener(this);
            sharebutton = (ImageButton) itemView.findViewById(R.id.shareButton);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }
    @SuppressLint("SimpleDateFormat")
    public static String getDateFormat(long date) {
        return new SimpleDateFormat("dd MMM yyyy").format(new Date(date));
    }
}