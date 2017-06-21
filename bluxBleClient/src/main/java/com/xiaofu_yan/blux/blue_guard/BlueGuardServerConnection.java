package com.xiaofu_yan.blux.blue_guard;

import android.content.Context;
import android.os.Bundle;

import com.xiaofu_yan.blux.le.client.BluxCsConnection;

import java.util.UUID;

public class BlueGuardServerConnection extends BluxCsConnection {

	public boolean connect(Context context) {
		return connectServer(context);
	}

	public void disconnect() {
		disconnectServer();
	}


	// BluxCsConnection overrides
	@Override
	protected void onConnected() {
		getServerRootObject();
	}

	@Override
	protected void onDisconnected() {
	}

	@Override
	protected void foundServerRootObject(UUID serverId, UUID clientId, Bundle msgData) {
	}
	
}
