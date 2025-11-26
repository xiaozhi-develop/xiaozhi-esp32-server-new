<template>
  <div class="welcome">
    <HeaderBar :devices="deviceList" @search="handleSearch" @search-reset="handleSearchReset" />

    <el-main style="padding: 20px;display: flex;flex-direction: column;">
      <div>
        <!-- Add Device Banner -->
        <div class="add-device">
          <div class="add-device-bg">
            <div class="hellow-text" style="margin-top: 30px;">
              {{ $t('home.greeting') }}
            </div>
            <div class="hellow-text">
              {{ $t('home.wish') }}
            </div>
            <div class="hi-hint">
              let's have a wonderful day!
            </div>
            <div class="add-device-btn">
              <div class="left-add" @click="handleAddDevice">
                {{ $t('device.add') }}
              </div>
              <div style="width: 23px;height: 13px;background: #5778ff;margin-left: -10px;" />
              <div class="right-add">
                <i class="el-icon-right" @click="handleAddDevice" style="font-size: 20px;color: #fff;" />
              </div>
            </div>
            <!-- Manual Add Button (Optional, maybe next to the main one or small link) -->
            <div class="manual-add-btn" @click="handleManualAddDevice">
              {{ $t('device.manualAdd') }}
            </div>
          </div>
        </div>

        <!-- Device Grid -->
        <div class="device-list-container" v-loading="loading">
          <div class="device-item" v-for="device in filteredDeviceList" :key="device.device_id">
            <div style="display: flex;justify-content: space-between;align-items: center;">
              <div style="font-weight: 700;font-size: 18px;text-align: left;color: #3d4566; display: flex; align-items: center; gap: 8px;">
                <span v-if="!device.isEdit" @click="device.isEdit = true">{{ device.remark || device.macAddress }}</span>
                <el-input v-else v-model="device.remark" size="mini" style="width: 150px;"
                  @blur="onRemarkBlur(device)" @keyup.enter.native="onRemarkEnter(device)" />
                <i v-if="!device.isEdit" class="el-icon-edit" style="font-size: 14px; color: #999; cursor: pointer;" @click="device.isEdit = true"></i>
              </div>
              <div>
                <el-tag v-if="device.deviceStatus === 'online'" type="success" size="mini">{{ $t('device.online') }}</el-tag>
                <el-tag v-else type="danger" size="mini">{{ $t('device.offline') }}</el-tag>
              </div>
            </div>
            
            <div class="device-info-row">
              <span class="label">{{ $t('device.model') }}:</span>
              <span class="value">{{ getFirmwareTypeName(device.model) }}</span>
            </div>
            <div class="device-info-row">
              <span class="label">{{ $t('device.macAddress') }}:</span>
              <span class="value">{{ device.macAddress }}</span>
            </div>
            <div class="device-info-row">
              <span class="label">{{ $t('device.firmwareVersion') }}:</span>
              <span class="value">{{ device.firmwareVersion }}</span>
            </div>
            <div class="device-info-row">
              <span class="label">记忆体:</span>
              <span class="value">
                 <el-select v-model="device.memoryId" size="mini" placeholder="请选择" @change="handleMemoryChange(device, $event)" style="width: 150px;">
                    <el-option label="无" value=""></el-option>
                    <el-option v-for="mem in memoryList" :key="mem.id" :label="mem.name" :value="mem.id"></el-option>
                 </el-select>
              </span>
            </div>

            <div class="action-buttons">
              <div class="settings-btn" @click="handleConfigAgent(device)">
                {{ $t('device.configAgent') }}
              </div>
              <div class="settings-btn delete-btn" @click="handleUnbind(device.device_id)">
                {{ $t('device.unbind') }}
              </div>
            </div>
            
            <div class="version-info">
              <div>{{ $t('device.lastConversation') }}：{{ formatTime(device.lastConversation) }}</div>
            </div>
          </div>
        </div>
      </div>
    </el-main>

    <AddDeviceDialog :visible.sync="addDeviceDialogVisible" @refresh="fetchDevices" />
    <ManualAddDeviceDialog :visible.sync="manualAddDeviceDialogVisible" @refresh="fetchDevices" />
    
    <!-- 智能体配置弹窗 -->
    <AgentConfigDialog 
      v-if="agentConfigVisible"
      :visible.sync="agentConfigVisible"
      :agent-id="currentConfigAgentId"
      :device-id="currentConfigDeviceId"
      @refresh="fetchDevices"
    />

  </div>
</template>

<script>
import Api from '@/apis/api';
import AddDeviceDialog from "@/components/AddDeviceDialog.vue";
import HeaderBar from "@/components/HeaderBar.vue";
import ManualAddDeviceDialog from "@/components/ManualAddDeviceDialog.vue";
import AgentConfigDialog from "@/components/AgentConfigDialog.vue";

export default {
  name: 'UserDeviceList',
  components: {
    HeaderBar,
    AddDeviceDialog,
    ManualAddDeviceDialog,
    AgentConfigDialog
  },
  data() {
    return {
      addDeviceDialogVisible: false,
      manualAddDeviceDialogVisible: false,
      agentConfigVisible: false,
      currentConfigAgentId: '',
      currentConfigDeviceId: '',
      searchRegex: null,
      deviceList: [],
      loading: false,
      firmwareTypes: [],
      memoryList: [],
    };
  },
  computed: {
    filteredDeviceList() {
      if (!this.searchRegex) return this.deviceList;
      return this.deviceList.filter(device => 
        this.searchRegex.test(device.remark) || 
        this.searchRegex.test(device.macAddress) ||
        this.searchRegex.test(device.model)
      );
    }
  },
  created() {
    this.getFirmwareTypes();
    this.fetchDevices();
    this.fetchMemories();
  },
  methods: {
    fetchMemories() {
      Api.memory.getMemoryList((res) => {
        if (res.data.code === 0) {
          this.memoryList = res.data.data;
        }
      });
    },
    handleMemoryChange(device, memoryId) {
       this.updateDeviceInfo(device.device_id, { memoryId: memoryId }, (ok, resp) => {
          if (ok) {
             this.$message.success("记忆体已更新");
          } else {
             this.$message.error("更新失败");
             device.memoryId = ""; // Reset or handle error
             this.fetchDevices(); // Reload to be safe
          }
       });
    },
    async getFirmwareTypes() {
      try {
        const res = await Api.dict.getDictDataByType('FIRMWARE_TYPE')
        this.firmwareTypes = res.data
      } catch (error) {
        console.error(this.$t('device.getFirmwareTypeFailed') + ':', error)
      }
    },
    handleSearch(regex) {
      this.searchRegex = regex;
    },
    handleSearchReset() {
      this.searchRegex = null;
    },
    handleAddDevice() {
      this.addDeviceDialogVisible = true;
    },
    handleManualAddDevice() {
      this.manualAddDeviceDialogVisible = true;
    },
    handleConfigAgent(row) {
      this.currentConfigAgentId = row.agentId;
      this.currentConfigDeviceId = row.device_id;
      this.agentConfigVisible = true;
    },
    submitRemark(row) {
      if (row._submitting) return;

      const text = (row.remark || '').trim();
      if (text.length > 64) {
        this.$message.warning(this.$t('device.remarkTooLong'));
        return;
      }
      if (text === row._originalRemark) {
        return;
      }

      row._submitting = true;
      this.updateDeviceInfo(row.device_id, { alias: text }, (ok, resp) => {
        if (ok) {
          row._originalRemark = text;
          this.$message.success(this.$t('device.remarkSaved'));
        } else {
          row.remark = row._originalRemark;
          this.$message.error(resp.msg || this.$t('device.remarkSaveFailed'));
        }
        row._submitting = false;
      });
    },
    onRemarkBlur(row) {
      row.isEdit = false;
      setTimeout(() => {
        this.submitRemark(row);
      }, 100);
    },
    onRemarkEnter(row) {
      row.isEdit = false;
      this.submitRemark(row);
    },
    handleUnbind(device_id) {
      this.$confirm(this.$t('device.confirmUnbind'), this.$t('message.warning'), {
        confirmButtonText: this.$t('button.ok'),
        cancelButtonText: this.$t('button.cancel'),
        type: 'warning'
      }).then(() => {
        Api.device.unbindDevice(device_id, ({ data }) => {
          if (data.code === 0) {
            this.$message.success({
              message: this.$t('device.unbindSuccess'),
              showClose: true
            });
            this.fetchDevices();
          } else {
            this.$message.error({
              message: data.msg || this.$t('device.unbindFailed'),
              showClose: true
            });
          }
        });
      });
    },
    fetchDevices() {
      this.loading = true;
      Api.device.getAllUserDevices(({ data }) => {
        this.loading = false;
        if (data.code === 0) {
          this.deviceList = data.data.map(device => {
            return {
              device_id: device.id,
              model: device.board,
              firmwareVersion: device.appVersion,
              macAddress: device.macAddress,
              bindTime: device.createDate,
              lastConversation: device.lastConnectedAt,
              remark: device.alias,
              agentId: device.agentId,
              memoryId: device.memoryId,
              _originalRemark: device.alias,
              isEdit: false,
              _submitting: false,
              otaSwitch: device.autoUpdate === 1,
              rawBindTime: new Date(device.createDate).getTime(),
              deviceStatus: 'offline'
            };
          })
            .sort((a, b) => a.rawBindTime - b.rawBindTime);
          
          this.fetchDeviceStatus();
        } else {
          this.$message.error(data.msg || this.$t('device.getListFailed'));
        }
      });
    },

    fetchDeviceStatus() {
      Api.device.getAllDevicesStatus(({ data }) => {
        if (data.code === 0) {
          try {
            const statusData = JSON.parse(data.data);
            if (statusData && typeof statusData === 'object') {
              this.updateDeviceStatusFromResponse(statusData);
            }
          } catch (error) {
            // ignore
          }
        }
      });
    },

    updateDeviceStatusFromResponse(deviceStatusMap) {
      this.deviceList.forEach(device => {
        const macAddress = device.macAddress ? device.macAddress.replace(/:/g, '_') : 'unknown';
        const groupId = device.model ? device.model.replace(/:/g, '_') : 'GID_default';
        const mqttClientId = `${groupId}@@@${macAddress}@@@${macAddress}`;

        if (deviceStatusMap[mqttClientId]) {
          const statusInfo = deviceStatusMap[mqttClientId];
          let isOnline = false;
          if (statusInfo.isAlive === true) {
            isOnline = true;
          } else if (statusInfo.isAlive === false) {
            isOnline = false;
          } else if (statusInfo.isAlive === null && statusInfo.exists === true) {
            isOnline = true;
          }
          device.deviceStatus = isOnline ? 'online' : 'offline';
        } else {
          device.deviceStatus = 'offline';
        }
      });
    },
    getFirmwareTypeName(type) {
      const firmwareType = this.firmwareTypes.find(item => item.key === type)
      return firmwareType ? firmwareType.name : type
    },
    updateDeviceInfo(device_id, payload, callback) {
      return Api.device.updateDeviceInfo(device_id, payload, ({ data }) => {
        callback(data.code === 0, data);
      })
    },
    formatTime(time) {
      if (!time) return this.$t('home.noConversation');
      const lastTime = new Date(time);
      const now = new Date();
      const diffMinutes = Math.floor((now - lastTime) / (1000 * 60));

      if (diffMinutes <= 1) {
        return this.$t('home.justNow');
      } else if (diffMinutes < 60) {
        return this.$t('home.minutesAgo', { minutes: diffMinutes });
      } else if (diffMinutes < 24 * 60) {
        const hours = Math.floor(diffMinutes / 60);
        const minutes = diffMinutes % 60;
        return this.$t('home.hoursAgo', { hours, minutes });
      } else {
        return time;
      }
    }
  }
};
</script>

<style scoped>
.welcome {
  min-width: 900px;
  min-height: 506px;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(145deg, #e6eeff, #eff0ff);
  background-size: cover;
  background-position: center;
}

.add-device {
  height: 195px;
  border-radius: 15px;
  position: relative;
  overflow: hidden;
  background: linear-gradient(269.62deg, #e0e6fd 0%, #cce7ff 49.69%, #d3d3fe 100%);
}

.add-device-bg {
  width: 100%;
  height: 100%;
  text-align: left;
  background-image: url("@/assets/home/main-top-bg.png");
  background-size: cover;
  background-position: center;
  box-sizing: border-box;
}

.hellow-text {
  margin-left: 75px;
  color: #3d4566;
  font-size: 33px;
  font-weight: 700;
  letter-spacing: 0;
}

.hi-hint {
  font-weight: 400;
  font-size: 12px;
  text-align: left;
  color: #818cae;
  margin-left: 75px;
  margin-top: 5px;
}

.add-device-btn {
  display: flex;
  align-items: center;
  margin-left: 75px;
  margin-top: 15px;
  cursor: pointer;
}

.left-add {
  width: 105px;
  height: 34px;
  border-radius: 17px;
  background: #5778ff;
  color: #fff;
  font-size: 14px;
  font-weight: 500;
  text-align: center;
  line-height: 34px;
}

.right-add {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: #5778ff;
  margin-left: -6px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.manual-add-btn {
  position: absolute;
  right: 20px;
  bottom: 20px;
  color: #5778ff;
  cursor: pointer;
  font-size: 14px;
  text-decoration: underline;
}

.device-list-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 30px;
  padding: 30px 0;
}

.device-item {
  width: 100%;
  border-radius: 20px;
  background: #fafcfe;
  padding: 22px;
  box-sizing: border-box;
  transition: transform 0.2s;
}

.device-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}

.device-info-row {
  margin: 7px 0;
  font-size: 12px;
  color: #3d4566;
  display: flex;
  justify-content: space-between;
}

.device-info-row .label {
  color: #818cae;
}

.action-buttons {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-top: 15px;
}

.settings-btn {
  font-weight: 500;
  font-size: 12px;
  color: #5778ff;
  background: #e6ebff;
  padding: 0 12px;
  height: 24px;
  line-height: 24px;
  cursor: pointer;
  border-radius: 14px;
}

.delete-btn {
  color: #ff4949;
  background: #ffe6e6;
}

.version-info {
  display: flex;
  justify-content: space-between;
  margin-top: 15px;
  font-size: 12px;
  color: #979db1;
  font-weight: 400;
}
</style>
