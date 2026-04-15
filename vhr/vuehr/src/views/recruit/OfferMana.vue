<template>
  <div>
    <div style="display: flex;justify-content: space-between">
      <div>
        <el-input placeholder="请输入候选人姓名搜索..." prefix-icon="el-icon-search"
                  clearable @clear="initOffers" style="width: 300px;margin-right: 10px"
                  v-model="searchName" @keydown.enter.native="initOffers"></el-input>
        <el-button icon="el-icon-search" type="primary" @click="initOffers">搜索</el-button>
        <el-button type="primary" @click="showAdvanceSearchView = !showAdvanceSearchView">
          <i :class="showAdvanceSearchView?'fa fa-angle-double-up':'fa fa-angle-double-down'"></i>
          高级搜索
        </el-button>
      </div>
      <div>
        <el-button type="primary" icon="el-icon-plus" @click="showAddOfferView">创建Offer</el-button>
      </div>
    </div>
    <transition name="slide-fade">
      <div v-show="showAdvanceSearchView"
           style="border: 1px solid #409eff;border-radius: 5px;padding: 10px;margin: 10px 0px;">
        <el-row>
          <el-col :span="5">
            Offer状态:
            <el-select v-model="searchValue.offerStatus" placeholder="Offer状态" size="mini" style="width: 120px;" clearable>
              <el-option label="待发送" value="待发送"></el-option>
              <el-option label="待确认" value="待确认"></el-option>
              <el-option label="已接受" value="已接受"></el-option>
              <el-option label="已拒绝" value="已拒绝"></el-option>
              <el-option label="已过期" value="已过期"></el-option>
            </el-select>
          </el-col>
          <el-col :span="6">
            创建时间:
            <el-date-picker v-model="searchValue.createDateScope" type="daterange" size="mini"
                            value-format="yyyy-MM-dd" range-separator="至" start-placeholder="开始日期"
                            end-placeholder="结束日期" style="width: 200px;"></el-date-picker>
          </el-col>
          <el-col :span="6">
            <el-button size="mini" @click="clearSearchValue">取消</el-button>
            <el-button size="mini" icon="el-icon-search" type="primary" @click="initOffers">搜索</el-button>
          </el-col>
        </el-row>
      </div>
    </transition>
    <div style="margin-top: 10px">
      <el-table :data="offers" stripe border v-loading="loading" style="width: 100%">
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="resume.name" fixed label="候选人" width="100"></el-table-column>
        <el-table-column prop="resume.phone" label="电话" width="120"></el-table-column>
        <el-table-column prop="resume.email" label="邮箱" width="180"></el-table-column>
        <el-table-column prop="position.name" label="入职职位" width="120"></el-table-column>
        <el-table-column prop="salary" label="薪资(元/月)" width="100"></el-table-column>
        <el-table-column prop="probationMonths" label="试用期(月)" width="90"></el-table-column>
        <el-table-column prop="probationSalary" label="试用期薪资" width="90"></el-table-column>
        <el-table-column prop="entryDate" label="入职日期" width="120"></el-table-column>
        <el-table-column prop="offerExpireDate" label="有效期至" width="120"></el-table-column>
        <el-table-column prop="offerStatus" label="Offer状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="getOfferStatusType(scope.row.offerStatus)">{{scope.row.offerStatus}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sendTime" label="发送状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.sendTime?'success':'info'">{{scope.row.sendTime?'已发送':'未发送'}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewStatus" label="查看状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.viewStatus?'success':'warning'">{{scope.row.viewStatus?'已查看':'未查看'}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sendTime" label="发送时间" width="160"></el-table-column>
        <el-table-column prop="confirmTime" label="确认时间" width="160"></el-table-column>
        <el-table-column prop="rejectReason" label="拒绝原因" width="150"></el-table-column>
        <el-table-column prop="createDate" label="创建时间" width="160"></el-table-column>
        <el-table-column fixed="right" label="操作" width="280">
          <template slot-scope="scope">
            <el-button @click="showEditOfferView(scope.row)" size="mini">编辑</el-button>
            <el-button @click="sendOffer(scope.row)" size="mini" type="primary" v-if="scope.row.offerStatus==='待发送'">发送</el-button>
            <el-button @click="showConfirmView(scope.row)" size="mini" type="success" v-if="scope.row.sendTime && scope.row.offerStatus==='待确认'">确认</el-button>
            <el-button @click="deleteOffer(scope.row)" size="mini" type="danger">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="display: flex;justify-content: flex-end;margin-top: 10px">
        <el-pagination background @current-change="currentChange" @size-change="sizeChange"
                       layout="sizes, prev, pager, next, jumper, ->, total, slot" :total="total"></el-pagination>
      </div>
    </div>
    <el-dialog :title="title" :visible.sync="dialogVisible" width="60%">
      <el-form :model="offer" :rules="rules" ref="offerForm">
        <el-row>
          <el-col :span="12">
            <el-form-item label="候选人:" prop="resumeId">
              <el-select v-model="offer.resumeId" placeholder="选择候选人" size="mini" style="width: 250px;" filterable>
                <el-option v-for="item in filteredResumes" :key="item.id" :label="item.name + ' - ' + item.phone" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入职职位:" prop="posId">
              <el-select v-model="offer.posId" placeholder="入职职位" size="mini" style="width: 250px;">
                <el-option v-for="item in positions" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="薪资(元/月):" prop="salary">
              <el-input-number v-model="offer.salary" :min="0" size="mini" style="width: 250px;"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="试用期(月):" prop="probationMonths">
              <el-input-number v-model="offer.probationMonths" :min="0" :max="6" size="mini" style="width: 250px;"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="试用期薪资:">
              <el-input-number v-model="offer.probationSalary" :min="0" size="mini" style="width: 250px;"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入职日期:" prop="entryDate">
              <el-date-picker v-model="offer.entryDate" size="mini" type="date"
                              value-format="yyyy-MM-dd" style="width: 250px;"></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="Offer有效期至:" prop="offerExpireDate">
              <el-date-picker v-model="offer.offerExpireDate" size="mini" type="date"
                              value-format="yyyy-MM-dd" style="width: 250px;"></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Offer状态:">
              <el-select v-model="offer.offerStatus" placeholder="Offer状态" size="mini" style="width: 250px;">
                <el-option label="待发送" value="待发送"></el-option>
                <el-option label="待确认" value="待确认"></el-option>
                <el-option label="已接受" value="已接受"></el-option>
                <el-option label="已拒绝" value="已拒绝"></el-option>
                <el-option label="已过期" value="已过期"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注:">
              <el-input type="textarea" :rows="2" v-model="offer.remark" placeholder="请输入备注"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="doAddOffer">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog title="Offer确认" :visible.sync="confirmDialogVisible" width="50%">
      <el-form :model="confirmForm" ref="confirmForm">
        <el-row>
          <el-col :span="24">
            <el-form-item label="候选人:">
              <span>{{currentOffer.resume ? currentOffer.resume.name : ''}}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="Offer状态:">
              <el-radio-group v-model="confirmForm.offerStatus">
                <el-radio label="已接受">已接受</el-radio>
                <el-radio label="已拒绝">已拒绝</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="confirmForm.offerStatus === '已拒绝'">
          <el-col :span="24">
            <el-form-item label="拒绝原因:">
              <el-input type="textarea" :rows="2" v-model="confirmForm.rejectReason" placeholder="请输入拒绝原因"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="confirmDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="doConfirmOffer">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog title="入职提醒" :visible.sync="entryRemindDialogVisible" width="50%">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="候选人">{{entryRemind.resumeName}}</el-descriptions-item>
        <el-descriptions-item label="入职日期">{{entryRemind.entryDate}}</el-descriptions-item>
        <el-descriptions-item label="入职职位">{{entryRemind.positionName}}</el-descriptions-item>
      </el-descriptions>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="entryRemindDialogVisible = false">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {getOfferByPage, addOffer, updateOffer, deleteOffer, sendOffer as sendOfferReq, confirmOffer} from '@/utils/offer'
import {getResumesByStatus} from '@/utils/resume'
import {getRequest} from '@/utils/api'
export default {
  name: 'OfferMana',
  data() {
    return {
      offers: [],
      loading: false,
      total: 0,
      page: 1,
      size: 10,
      searchName: '',
      showAdvanceSearchView: false,
      searchValue: {
        offerStatus: '',
        createDateScope: null
      },
      dialogVisible: false,
      title: '',
      offer: {
        resumeId: null,
        posId: null,
        salary: null,
        probationMonths: 3,
        probationSalary: null,
        entryDate: '',
        offerExpireDate: '',
        offerStatus: '待发送',
        remark: ''
      },
      rules: {
        resumeId: [{required: true, message: '请选择候选人', trigger: 'change'}],
        posId: [{required: true, message: '请选择入职职位', trigger: 'change'}],
        salary: [{required: true, message: '请输入薪资', trigger: 'blur'}],
        entryDate: [{required: true, message: '请选择入职日期', trigger: 'change'}],
        offerExpireDate: [{required: true, message: '请选择有效期', trigger: 'change'}]
      },
      resumes: [],
      positions: [],
      confirmDialogVisible: false,
      confirmForm: {
        id: null,
        offerStatus: '已接受',
        rejectReason: ''
      },
      currentOffer: {},
      entryRemindDialogVisible: false,
      entryRemind: {}
    }
  },
  computed: {
    filteredResumes() {
      return this.resumes.filter(r => r.status === '已筛选' || r.status === '待面试')
    }
  },
  mounted() {
    this.initOffers()
    this.initResumes()
    this.initPositions()
    if (this.$route.query.resumeId) {
      this.offer.resumeId = parseInt(this.$route.query.resumeId)
      this.showAddOfferView()
    }
  },
  methods: {
    initOffers() {
      this.loading = true
      let params = {
        page: this.page,
        size: this.size,
        offerStatus: this.searchValue.offerStatus
      }
      if (this.searchValue.createDateScope) {
        params.createDateScope = this.searchValue.createDateScope.join(',')
      }
      getOfferByPage(params).then(resp => {
        this.loading = false
        if (resp) {
          this.offers = resp.data
          this.total = resp.total
        }
      })
    },
    initResumes() {
      getResumesByStatus('已筛选').then(resp => {
        if (resp) {
          this.resumes = resp
        }
      })
    },
    initPositions() {
      getRequest('/system/basic/pos/').then(resp => {
        if (resp) {
          this.positions = resp
        }
      })
    },
    currentChange(page) {
      this.page = page
      this.initOffers()
    },
    sizeChange(size) {
      this.size = size
      this.initOffers()
    },
    showAddOfferView() {
      this.title = '创建Offer'
      this.offer = {
        resumeId: this.$route.query.resumeId ? parseInt(this.$route.query.resumeId) : null,
        posId: null,
        salary: null,
        probationMonths: 3,
        probationSalary: null,
        entryDate: '',
        offerExpireDate: '',
        offerStatus: '待发送',
        remark: ''
      }
      this.dialogVisible = true
    },
    showEditOfferView(data) {
      this.title = '编辑Offer'
      this.offer = Object.assign({}, data)
      this.dialogVisible = true
    },
    doAddOffer() {
      if (this.offer.id) {
        updateOffer(this.offer).then(resp => {
          if (resp) {
            this.dialogVisible = false
            this.initOffers()
          }
        })
      } else {
        addOffer(this.offer).then(resp => {
          if (resp) {
            this.dialogVisible = false
            this.initOffers()
          }
        })
      }
    },
    deleteOffer(data) {
      this.$confirm('此操作将删除该Offer, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteOffer(data.id).then(resp => {
          if (resp) {
            this.initOffers()
          }
        })
      }).catch(() => {})
    },
    sendOffer(data) {
      this.$confirm('确认发送Offer给候选人?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        sendOfferReq(data.id).then(resp => {
          if (resp) {
            this.initOffers()
          }
        })
      }).catch(() => {})
    },
    showConfirmView(data) {
      this.currentOffer = data
      this.confirmForm = {
        id: data.id,
        offerStatus: '已接受',
        rejectReason: ''
      }
      this.confirmDialogVisible = true
    },
    doConfirmOffer() {
      confirmOffer(this.confirmForm).then(resp => {
        if (resp) {
          this.confirmDialogVisible = false
          this.initOffers()
          if (this.confirmForm.offerStatus === '已接受') {
            this.showEntryRemind()
          }
        }
      })
    },
    showEntryRemind() {
      this.entryRemind = {
        resumeName: this.currentOffer.resume ? this.currentOffer.resume.name : '',
        entryDate: this.currentOffer.entryDate,
        positionName: this.currentOffer.position ? this.currentOffer.position.name : ''
      }
      this.entryRemindDialogVisible = true
    },
    clearSearchValue() {
      this.searchValue = {
        offerStatus: '',
        createDateScope: null
      }
      this.searchName = ''
      this.initOffers()
    },
    getOfferStatusType(status) {
      switch (status) {
        case '待发送': return 'info'
        case '待确认': return 'warning'
        case '已接受': return 'success'
        case '已拒绝': return 'danger'
        case '已过期': return 'info'
        default: return 'info'
      }
    }
  }
}
</script>

<style scoped>
.slide-fade-enter-active {
  transition: all .8s ease;
}
.slide-fade-leave-active {
  transition: all .8s cubic-bezier(1.0, 0.5, 0.8, 1.0);
}
.slide-fade-enter, .slide-fade-leave-to {
  transform: translateX(10px);
  opacity: 0;
}
</style>
