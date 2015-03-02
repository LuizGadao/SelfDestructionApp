package br.com.luizgadao.selfdestruction.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;

import br.com.luizgadao.selfdestruction.R;
import br.com.luizgadao.selfdestruction.utils.ParseConstants;
import br.com.luizgadao.selfdestruction.utils.Utils;

/**
 * Created by luizcarlos on 02/03/15.
 */
public class MessageAdapter extends ArrayAdapter<ParseObject> {

    private Context context;
    private List<ParseObject> messages;

    public MessageAdapter( Context context, List<ParseObject> messages ) {
        super( context, R.layout.message_item, messages );
        this.context = context;
        this.messages = messages;
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent ) {

        ViewHolder holder;

        if ( convertView == null )
        {
            convertView = LayoutInflater.from( context ).inflate( R.layout.message_item, null );
            holder = new ViewHolder( convertView );
            convertView.setTag( holder );
        }
        else
        {
            holder = ( ViewHolder ) convertView.getTag();
        }

        ParseObject message = messages.get( position );
        if ( message.getString( ParseConstants.KEY_FILE_TYPE ).equals( ParseConstants.KEY_FILE_IMAGE ) )
            holder.icon.setImageResource( R.drawable.ic_action_picture );
        else
            holder.icon.setImageResource( R.drawable.ic_action_play_over_video );

        holder.tvSender.setText( message.getString( ParseConstants.KEY_SENDER_NAME ) );
        holder.tvCreatedAt.setText( Utils.getTimeCreated( message.getCreatedAt(), context.getResources() ) + " ago." );

        return convertView;
    }


    private static class ViewHolder
    {
        ImageView icon;
        TextView tvSender, tvCreatedAt;

        private ViewHolder( View view ) {
            this.icon = ( ImageView ) view.findViewById( R.id.iv_type_message );
            this.tvSender = ( TextView ) view.findViewById( R.id.tv_sender );
            this.tvCreatedAt = ( TextView ) view.findViewById( R.id.tv_created_at );
        }
    }
}
