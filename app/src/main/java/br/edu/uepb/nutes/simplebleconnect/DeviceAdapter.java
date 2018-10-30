package br.edu.uepb.nutes.simplebleconnect;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/*
 * Copyright (c) 2018 NUTES/UEPB
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceAdapterViewHolder> {

    public static ClickDevice clickInterface;
    Context mctx;
    private List<Device> mList;
    private int type;

    public DeviceAdapter(Context ctx, List<Device> list, ClickDevice clickInterface, int type) {
        this.mctx = ctx;
        this.mList = list;
        this.clickInterface = clickInterface;
        this.type = type;
    }

    @Override
    public DeviceAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_device, viewGroup, false);
        return new DeviceAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DeviceAdapterViewHolder viewHolder, int i) {
        Device device = mList.get(i);

        viewHolder.deviceName.setText(device.getName());
        viewHolder.deviceManufactured.setText(device.getManufactured());
        viewHolder.deviceModel.setText(device.getModel());
        viewHolder.deviceAddress.setText(device.getAddress());
        viewHolder.deviceService.setText(device.getValue());
        viewHolder.deviceRSSI.setText(device.getRSSI());

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    protected class DeviceAdapterViewHolder extends RecyclerView.ViewHolder {

        protected TextView deviceName;
        protected TextView deviceManufactured;
        protected TextView deviceModel;
        protected TextView deviceAddress;
        protected TextView deviceService;
        protected TextView deviceRSSI;

        public DeviceAdapterViewHolder(final View itemView) {
            super(itemView);

            deviceName = (TextView) itemView.findViewById(R.id.device_name);
            deviceManufactured = (TextView) itemView.findViewById(R.id.device_manufactured);
            deviceModel = (TextView) itemView.findViewById(R.id.device_model);
            deviceAddress = (TextView) itemView.findViewById(R.id.device_address);
            deviceService = (TextView) itemView.findViewById(R.id.device_value);
            deviceRSSI = (TextView) itemView.findViewById(R.id.device_rssi);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickInterface.onClickDevice(mList.get(getLayoutPosition()), type);
                }
            });
        }
    }
}