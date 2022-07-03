package com.familheey.app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.R;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE;
import static com.familheey.app.Utilities.Constants.Paths.PROFILE_PIC;

public class FamilyMembersAdapter extends RecyclerView.Adapter<FamilyMembersAdapter.ViewHolder> {


    private final List<FamilyMember> familyMembers;
    private final OnFamilyMemberCallback mListener;
    private Family family;
    private final Context context;
    private String signedInUserId = "";

    public FamilyMembersAdapter(OnFamilyMemberCallback mListener, Context context, List<FamilyMember> familyMembers, Family family) {
        this.familyMembers = familyMembers;
        this.context = context;
        this.mListener = mListener;
        this.family = family;
        signedInUserId = SharedPref.getUserRegistration().getId();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_family_member, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final FamilyMember familyMember = familyMembers.get(position);

        if (familyMember.isPrimaryAdmin())
            holder.memberOptions.setVisibility(View.VISIBLE);
        else
            holder.memberOptions.setVisibility(View.INVISIBLE);
        holder.addRoleImage.setClickable(true);
        holder.addRole.setClickable(true);
        holder.addRoleImage.setVisibility(View.VISIBLE);
        holder.addRole.setVisibility(View.VISIBLE);
        Glide.with(holder.itemView.getContext()).load(R.drawable.icon_edit_pencil).into(holder.addRoleImage);
        Glide.with(context)
                .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + PROFILE_PIC + familyMember.getProPic())
                .apply(Utilities.getCurvedRequestOptions())
                .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                .placeholder(R.drawable.avatar_male)
                .into(holder.memberImage);
        holder.memberName.setText(familyMember.getFullName());
        holder.memberType.setText(familyMember.getUserType());
        holder.memberLocation.setText(getMemberSinceAsFormatted(familyMember));
        if (family.isRegularGroup()) {
            holder.addRoleImage.setVisibility(View.VISIBLE);
            holder.addRole.setVisibility(View.VISIBLE);
            if (familyMember.getRelationShip() != null && familyMember.getRelationShip().length() > 0)
                holder.addRole.setText(familyMember.getRelationShip());
            else
                holder.addRole.setText("Add Relationship");
        } else {
            if (familyMember.getRelationShip() != null && familyMember.getRelationShip().length() > 0)
                holder.addRole.setText(familyMember.getRelationShip());
            else
                holder.addRole.setText("Add Role");
            if (familyMember.isPrimaryAdmin()) {
                Glide.with(holder.itemView.getContext()).load(R.drawable.icon_edit_pencil).into(holder.addRoleImage);
                holder.addRoleImage.setVisibility(View.VISIBLE);
                holder.addRole.setVisibility(View.VISIBLE);
            } else {
                if (String.valueOf(familyMember.getUserId()).equals(SharedPref.getUserRegistration().getId())) {
                    Glide.with(holder.itemView.getContext()).load(R.drawable.icon_edit_pencil).into(holder.addRoleImage);
                    holder.addRoleImage.setVisibility(View.VISIBLE);
                    holder.addRole.setVisibility(View.VISIBLE);
                } else {
                    Glide.with(holder.itemView.getContext()).load(R.drawable.icon_edit_without_pencil).into(holder.addRoleImage);
                    if (familyMember.getRelationShip() != null && familyMember.getRelationShip().length() > 0) {
                        holder.addRole.setVisibility(View.VISIBLE);
                        holder.addRoleImage.setVisibility(View.VISIBLE);
                        holder.addRoleImage.setClickable(false);
                        holder.addRole.setClickable(false);
                    } else {
                        holder.addRole.setVisibility(View.INVISIBLE);
                        holder.addRoleImage.setVisibility(View.INVISIBLE);
                    }
                }
            }
            //Code to display 3 Dots

            if (familyMember.getUserId().toString().equalsIgnoreCase(signedInUserId))
                holder.memberOptions.setVisibility(View.INVISIBLE);
        }
        // Write Code above the if else statement
    }

    @Override
    public int getItemCount() {
        return familyMembers.size();
    }

    private void showMenus(View v, int position) {
        FamilyMember familyMember = familyMembers.get(position);
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.popup_menu_family_member, popup.getMenu());
        Menu m = popup.getMenu();
        if (familyMember.getIsPrimarAdmin() == null || familyMember.getIsPrimarAdmin().equalsIgnoreCase("member")) {
            m.removeItem(R.id.menuBlock);
            m.removeItem(R.id.menuRemove);
        } else if (familyMember.getCrntUserId().equalsIgnoreCase(familyMember.getUserId().toString())) {
            m.removeItem(R.id.menuBlock);
            m.removeItem(R.id.menuRemove);
        } else {
            if (familyMember.getIsBlocked() != null && familyMember.getIsBlocked()) {
                popup.getMenu().getItem(1).setTitle("Un Block");
            }
        }

        if (familyMember.isAdmin()) {
            popup.getMenu().getItem(0).setTitle("Remove as Admin");
        }

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menumakeAdmin:
                    mListener.makeAdmin(familyMember);
                    break;
                case R.id.menuBlock:
                    if (familyMember.getIsBlocked() != null && familyMember.getIsBlocked()) {
                        mListener.unBlockMember(familyMember);
                    } else {
                        mListener.blockMember(familyMember);
                    }

                    break;
                case R.id.menuRemove:
                    mListener.removeMember(familyMember);
                    break;
                case R.id.menuPaymentHistory:
                    mListener.showPaymentHistory(familyMember);
                    break;
            }
            return true;
        });

        popup.show();
    }

    public interface OnFamilyMemberCallback {

        void onMemberSelected(FamilyMember familyMember);

        void addMemberRelationShip(FamilyMember familyMember);

        void updateRelationShip(FamilyMember familyMember);

        void blockMember(FamilyMember familyMember);

        void unBlockMember(FamilyMember familyMember);

        void removeMember(FamilyMember familyMember);

        void makeAdmin(FamilyMember familyMember);

        void showPaymentHistory(FamilyMember familyMember);
    }

    private String getMemberSinceAsFormatted(FamilyMember familyMember) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date date = simpleDateFormat.parse(familyMember.getMemberSince());
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault());
            return "Member since " + sdf.format(date);
        } catch (ParseException | NullPointerException e) {
            e.printStackTrace();
            if (familyMember.getMemberSince() == null)
                return "Member since -";
            else return familyMember.getMemberSince();
        }
    }

    public void updateFamily(Family family) {
        this.family = family;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.memberImage)
        ImageView memberImage;
        @BindView(R.id.memberName)
        TextView memberName;
        @BindView(R.id.memberLocation)
        TextView memberLocation;
        @BindView(R.id.memberOptions)
        ImageView memberOptions;
        @BindView(R.id.memberTypeBackground)
        ImageView memberTypeBackground;
        @BindView(R.id.memberType)
        TextView memberType;
        @BindView(R.id.addRole)
        TextView addRole;
        @BindView(R.id.addRoleImage)
        ImageView addRoleImage;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this::onClick);
            memberOptions.setOnClickListener(this::onClick);
            addRole.setOnClickListener(this::onClick);
            addRoleImage.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.addRole:
                case R.id.addRoleImage:
                    if (familyMembers.get(getAdapterPosition()).getRelationShip() != null && familyMembers.get(getAdapterPosition()).getRelationShip().length() > 0) {
                        mListener.updateRelationShip(familyMembers.get(getAdapterPosition()));
                    } else
                        mListener.addMemberRelationShip(familyMembers.get(getAdapterPosition()));
                    break;
                case R.id.memberOptions:
                    // mListener.onMemberOptionsSelected(familyMembers.get(getAdapterPosition()));
                    showMenus(v, getAdapterPosition());
                    break;
                default:
                    mListener.onMemberSelected(familyMembers.get(getAdapterPosition()));
            }
        }
    }
}