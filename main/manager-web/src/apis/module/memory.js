import { getServiceUrl } from '../api';
import RequestService from '../httpRequest';

export default {
  // 获取记忆体列表
  getMemoryList(callback) {
    RequestService.sendRequest()
      .url(`${getServiceUrl()}/memory/list`)
      .method('GET')
      .success((res) => {
        RequestService.clearRequestTime();
        callback(res);
      })
      .networkFail((err) => {
        console.error('获取记忆体列表失败:', err);
        RequestService.reAjaxFun(() => {
          this.getMemoryList(callback);
        });
      }).send();
  },
  // 获取记忆体详情
  getMemory(id, callback) {
    RequestService.sendRequest()
      .url(`${getServiceUrl()}/memory/${id}`)
      .method('GET')
      .success((res) => {
        RequestService.clearRequestTime();
        callback(res);
      })
      .networkFail((err) => {
        console.error('获取记忆体详情失败:', err);
        RequestService.reAjaxFun(() => {
          this.getMemory(id, callback);
        });
      }).send();
  },
  // 保存记忆体
  saveMemory(data, callback) {
    RequestService.sendRequest()
      .url(`${getServiceUrl()}/memory`)
      .method('POST')
      .data(data)
      .success((res) => {
        RequestService.clearRequestTime();
        callback(res);
      })
      .networkFail((err) => {
        console.error('保存记忆体失败:', err);
        RequestService.reAjaxFun(() => {
          this.saveMemory(data, callback);
        });
      }).send();
  },
  // 修改记忆体
  updateMemory(data, callback) {
    RequestService.sendRequest()
      .url(`${getServiceUrl()}/memory`)
      .method('PUT')
      .data(data)
      .success((res) => {
        RequestService.clearRequestTime();
        callback(res);
      })
      .networkFail((err) => {
        console.error('修改记忆体失败:', err);
        RequestService.reAjaxFun(() => {
          this.updateMemory(data, callback);
        });
      }).send();
  },
  // 删除记忆体
  deleteMemory(ids, callback) {
    RequestService.sendRequest()
      .url(`${getServiceUrl()}/memory`)
      .method('DELETE')
      .data(ids)
      .success((res) => {
        RequestService.clearRequestTime();
        callback(res);
      })
      .networkFail((err) => {
        console.error('删除记忆体失败:', err);
        RequestService.reAjaxFun(() => {
          this.deleteMemory(ids, callback);
        });
      }).send();
  }
}
