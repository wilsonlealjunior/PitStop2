package pitstop.com.br.pitstop;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

import pitstop.com.br.pitstop.pockdata.PocketPos;
import pitstop.com.br.pitstop.util.FontDefine;
import pitstop.com.br.pitstop.util.Printer;
import pitstop.com.br.pitstop.util.StringUtil;
import pitstop.com.br.pitstop.util.Util;


/**
 * Created by ramon on 26/01/18.
 */

public class Print {
    private Context context;
    private String conteudo;

    private ProgressDialog mConnectingDlg;
    private BluetoothAdapter mBluetoothAdapter;
    private P25Connector mConnector;
    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();

    public Print(Context context, String conteudo) {
        this.context = context;
        this.conteudo = conteudo;
    }

    public void imprime() {
        initBluetooth();
    }

    public void initBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            showUnsupported();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                showDisabled();
            } else {
                showEnabled();
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                if (pairedDevices != null) {
                    mDeviceList.addAll(pairedDevices);

                    updateDeviceList();
                }
            }
        }

        mConnectingDlg = new ProgressDialog(context);

        mConnectingDlg.setMessage("Conectando...");
        mConnectingDlg.setCancelable(false);

        mConnector = new P25Connector(new P25Connector.P25ConnectionListener() {
            @Override
            public void onStartConnecting() {
                mConnectingDlg.show();
            }

            @Override
            public void onConnectionSuccess() {
                mConnectingDlg.dismiss();

                showConnected();
                printText(conteudo);
            }

            @Override
            public void onConnectionFailed(String error) {
                mConnectingDlg.dismiss();
            }

            @Override
            public void onConnectionCancelled() {
                mConnectingDlg.dismiss();
            }

            @Override
            public void onDisconnected() {
                showDisonnected();
            }

        });


    }

    private void updateDeviceList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Dispositivos");
        builder.setCancelable(false);
        final String[] listDispositivos = getArray(mDeviceList);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, listDispositivos);

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mDeviceList == null || mDeviceList.size() == 0) {
                    return;
                }
                BluetoothDevice device = mDeviceList.get(which);

                if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    try {
                        createBond(device);
                    } catch (Exception e) {
                        showToast("Failed to pair device");
                        return;
                    }
                }
                try {
                    if (!mConnector.isConnected()) {
                        mConnector.connect(device);
                    } else {
                        mConnector.disconnect();

                        showDisonnected();
                    }
                } catch (P25ConnectionException e) {
                    e.printStackTrace();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String[] getArray(ArrayList<BluetoothDevice> data) {
        String[] list = new String[0];

        if (data == null) return list;

        int size = data.size();
        list = new String[size];

        for (int i = 0; i < size; i++) {
            list[i] = data.get(i).getName();
        }

        return list;
    }

    private void createBond(BluetoothDevice device) throws Exception {

        try {
            Class<?> cl = Class.forName("android.bluetooth.BluetoothDevice");
            Class<?>[] par = {};

            Method method = cl.getMethod("createBond", par);

            method.invoke(device);

        } catch (Exception e) {
            e.printStackTrace();
            showToast("erro");

            throw e;
        }
    }

    private void sendData(byte[] bytes) {
        try {
            mConnector.sendData(bytes);
        } catch (P25ConnectionException e) {
            e.printStackTrace();
        }
    }

    private void printText(String text) {
        byte[] line = Printer.printfont(text + "\n\n", FontDefine.FONT_32PX, FontDefine.Align_LEFT, (byte) 0x1A,
                PocketPos.LANGUAGE_PORTUGUESE);
        String tperaeste = StringUtil.toString(line);
//        byte[] senddata = PocketPos.FramePack(PocketPos.FRAME_TOF_PRINT, line, 0, line.length);
//        String teste2 = StringUtil.toString(senddata);

        sendData(line);
    }

    private void showConnected() {
        showToast("Connected");
    }

    private void showDisonnected() {
        showToast("Disconnected");
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private void showDisabled() {
        showToast("Bluetooth disabled");
    }

    private void showEnabled() {
        showToast("Bluetooth enabled");
    }

    private void showUnsupported() {
        showToast("Bluetooth is unsupported by this device");
    }

}
