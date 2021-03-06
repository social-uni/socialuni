/**
 * 举报内容的api
 */
import request from '@/plugins/request'
import TencentCosAuditHistoryRO from '@/model/audit/TencentCosAuditHistoryRO'

export default class AuditAPI {
  public static queryImgAuditHistoryAPI(data) {
    return request.post<TencentCosAuditHistoryRO[]>('audit/queryImgAuditHistory', data)
  }

  public static auditImgListAPI(data) {
    return request.post<TencentCosAuditHistoryRO[]>('audit/auditImgList', data)
  }
}
