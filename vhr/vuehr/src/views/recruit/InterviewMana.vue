<template>
  <div>
    <div style="display: flex;justify-content: space-between">
      <div>
        <el-input placeholder="请输入候选人姓名搜索..." prefix-icon="el-icon-search"
                  clearable @clear="initInterviews" style="width: 300px;margin-right: 10px"
                  v-model="searchName" @keydown.enter.native="initInterviews"></el-input>
        <el-button icon="el-icon-search" type="primary" @click="initInterviews">搜索</el-button>
        <el-button type="primary" @click="showAdvanceSearchView = !showAdvanceSearchView">
          <i :class="showAdvanceSearchView?'fa fa-angle-double-up':'fa fa-angle-double-down'"></i>
          高级搜索
        </el-button>
      </div>
      <div>
        <el-button type="primary" icon="el-icon-plus" @click="showAddInterviewView">安排面试</el-button>
      </div>
    </div>
    <transition name="slide-fade">
      <div v-show="showAdvanceSearchView"
           style="border: 1px solid #409eff;border-radius: 5px;padding: 10px;margin: 10px 0px;">
        <el-row>
          <el-col :span="5">
            面试类型:
            <el-select v-model="searchValue.interviewType" placeholder="面试类型" size="mini" style="width: 120px;" clearable>
              <el-option label="初面" value="初面"></el-option>
              <el-option label="复面" value="复面"></el-option>
              <el-option label="终面" value="终面"></el-option>
            </el-select>
          </el-col>
          <el-col :span="5">
            面试状态:
            <el-select v-model="searchValue.interviewStatus" placeholder="面试状态" size="mini" style="width: 120px;" clearable>
              <el-option label="待面试" value="待面试"></el-option>
              <el-option label="已面试" value="已面试"></el-option>
              <el-option label="已取消" value="已取消"></el-option>
            </el-select>
          </el-col>
          <el-col :span="5">
            面试结果:
            <el-select v-model="searchValue.interviewResult" placeholder="面试结果" size="mini" style="width: 120px;" clearable>
              <el-option label="通过" value="通过"></el-option>
              <el-option label="未通过" value="未通过"></el-option>
              <el-option label="待定" value="待定"></el-option>
            </el-select>
          </el-col>
          <el-col :span="6">
            面试时间:
            <el-date-picker v-model="searchValue.interviewTimeScope" type="daterange" size="mini"
                            value-format="yyyy-MM-dd" range-separator="至" start-placeholder="开始日期"
                            end-placeholder="结束日期" style="width: 200px;"></el-date-picker>
          </el-col>
        </el-row>
        <el-row style="margin-top: 10px">
          <el-button size="mini" @click="clearSearchValue">取消</el-button>
          <el-button size="mini" icon="el-icon-search" type="primary" @click="initInterviews">搜索</el-button>
        </el-row>
      </div>
    </transition>
    <div style="margin-top: 10px">
      <el-table :data="interviews" stripe border v-loading="loading" style="width: 100%">
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="resume.name" fixed label="候选人" width="100"></el-table-column>
        <el-table-column prop="resume.phone" label="电话" width="120"></el-table-column>
        <el-table-column prop="position.name" label="面试职位" width="120"></el-table-column>
        <el-table-column prop="interviewType" label="面试类型" width="80"></el-table-column>
        <el-table-column prop="interviewTime" label="面试时间" width="160"></el-table-column>
        <el-table-column prop="interviewAddress" label="面试地点" width="200"></el-table-column>
        <el-table-column prop="interviewerName" label="面试官" width="100"></el-table-column>
        <el-table-column prop="interviewStatus" label="面试状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="getInterviewStatusType(scope.row.interviewStatus)">{{scope.row.interviewStatus}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="interviewResult" label="面试结果" width="80">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.interviewResult" :type="getResultType(scope.row.interviewResult)">{{scope.row.interviewResult}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="评分" width="60"></el-table-column>
        <el-table-column prop="hireSuggestion" label="录用建议" width="100"></el-table-column>
        <el-table-column prop="notifyStatus" label="通知状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.notifyStatus?'success':'info'">{{scope.row.notifyStatus?'已通知':'未通知'}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createDate" label="创建时间" width="160"></el-table-column>
        <el-table-column fixed="right" label="操作" width="250">
          <template slot-scope="scope">
            <el-button @click="showEditInterviewView(scope.row)" size="mini">编辑</el-button>
            <el-button @click="showResultView(scope.row)" size="mini" type="primary" v-if="scope.row.interviewStatus==='待面试'">填写结果</el-button>
            <el-button @click="sendNotify(scope.row)" size="mini" type="warning" v-if="!scope.row.notifyStatus">通知</el-button>
            <el-button @click="deleteInterview(scope.row)" size="mini" type="danger">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="display: flex;justify-content: flex-end;margin-top: 10px">
        <el-pagination background @current-change="currentChange" @size-change="sizeChange"
                       layout="sizes, prev, pager, next, jumper, ->, total, slot" :total="total"></el-pagination>
      </div>
    </div>
    <el-dialog :title="title" :visible.sync="dialogVisible" width="60%">
      <el-form :model="interview" :rules="rules" ref="interviewForm">
        <el-row>
          <el-col :span="12">
            <el-form-item label="候选人:" prop="resumeId">
              <el-select v-model="interview.resumeId" placeholder="选择候选人" size="mini" style="width: 250px;" filterable>
                <el-option v-for="item in filteredResumes" :key="item.id" :label="item.name + ' - ' + item.phone" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="面试职位:" prop="posId">
              <el-select v-model="interview.posId" placeholder="面试职位" size="mini" style="width: 250px;">
                <el-option v-for="item in positions" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="面试类型:" prop="interviewType">
              <el-select v-model="interview.interviewType" placeholder="面试类型" size="mini" style="width: 250px;">
                <el-option label="初面" value="初面"></el-option>
                <el-option label="复面" value="复面"></el-option>
                <el-option label="终面" value="终面"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="面试时间:" prop="interviewTime">
              <el-date-picker v-model="interview.interviewTime" size="mini" type="datetime"
                              value-format="yyyy-MM-dd HH:mm:ss" style="width: 250px;"></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="面试地点:" prop="interviewAddress">
              <el-input size="mini" style="width: 250px" v-model="interview.interviewAddress" placeholder="请输入面试地点"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="面试官:" prop="interviewerId">
              <el-select v-model="interview.interviewerId" placeholder="面试官" size="mini" style="width: 250px;" @change="handleInterviewerChange">
                <el-option v-for="item in hrs" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注:">
              <el-input type="textarea" :rows="2" v-model="interview.remark" placeholder="请输入备注"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="doAddInterview">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog title="填写面试结果" :visible.sync="resultDialogVisible" width="60%">
      <el-form :model="resultForm" ref="resultForm">
        <el-row>
          <el-col :span="12">
            <el-form-item label="面试结果:">
              <el-select v-model="resultForm.interviewResult" placeholder="面试结果" size="mini" style="width: 200px;">
                <el-option label="通过" value="通过"></el-option>
                <el-option label="未通过" value="未通过"></el-option>
                <el-option label="待定" value="待定"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="面试评分:">
              <el-rate v-model="resultForm.score" :max="10" show-score></el-rate>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="录用建议:">
              <el-select v-model="resultForm.hireSuggestion" placeholder="录用建议" size="mini" style="width: 200px;">
                <el-option label="强烈推荐" value="强烈推荐"></el-option>
                <el-option label="推荐" value="推荐"></el-option>
                <el-option label="一般" value="一般"></el-option>
                <el-option label="不推荐" value="不推荐"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="优点:">
              <el-input type="textarea" :rows="2" v-model="resultForm.advantage" placeholder="请输入优点"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="缺点:">
              <el-input type="textarea" :rows="2" v-model="resultForm.disadvantage" placeholder="请输入缺点"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="面试评价:">
              <el-input type="textarea" :rows="3" v-model="resultForm.evaluation" placeholder="请输入面试评价"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="resultDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="doSubmitResult">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog title="面试统计" :visible.sync="statisticsDialogVisible" width="60%">
      <el-table :data="statistics" stripe border style="width: 100%">
        <el-table-column prop="positionName" label="职位"></el-table-column>
        <el-table-column prop="totalCount" label="面试人数"></el-table-column>
        <el-table-column prop="passCount" label="通过人数"></el-table-column>
        <el-table-column prop="failCount" label="未通过人数"></el-table-column>
        <el-table-column prop="pendingCount" label="待定人数"></el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import {getInterviewByPage, addInterview, updateInterview, deleteInterview, updateInterviewResult, updateNotifyStatus, getInterviewStatistics} from '@/utils/interview'
import {getResumesByStatus} from '@/utils/resume'
import {getRequest} from '@/utils/api'
export default {
  name: 'InterviewMana',
  data() {
    return {
      interviews: [],
      loading: false,
      total: 0,
      page: 1,
      size: 10,
      searchName: '',
      showAdvanceSearchView: false,
      searchValue: {
        interviewType: '',
        interviewStatus: '',
        interviewResult: '',
        interviewTimeScope: null
      },
      dialogVisible: false,
      title: '',
      interview: {
        resumeId: null,
        posId: null,
        interviewType: '初面',
        interviewTime: '',
        interviewAddress: '',
        interviewerId: null,
        interviewerName: '',
        remark: ''
      },
      rules: {
        resumeId: [{required: true, message: '请选择候选人', trigger: 'change'}],
        posId: [{required: true, message: '请选择面试职位', trigger: 'change'}],
        interviewType: [{required: true, message: '请选择面试类型', trigger: 'change'}],
        interviewTime: [{required: true, message: '请选择面试时间', trigger: 'change'}],
        interviewAddress: [{required: true, message: '请输入面试地点', trigger: 'blur'}],
        interviewerId: [{required: true, message: '请选择面试官', trigger: 'change'}]
      },
      resumes: [],
      positions: [],
      hrs: [],
      resultDialogVisible: false,
      resultForm: {
        id: null,
        interviewResult: '',
        score: 5,
        evaluation: '',
        advantage: '',
        disadvantage: '',
        hireSuggestion: ''
      },
      statisticsDialogVisible: false,
      statistics: []
    }
  },
  computed: {
    filteredResumes() {
      return this.resumes.filter(r => r.status === '已筛选' || r.status === '待面试')
    }
  },
  mounted() {
    this.initInterviews()
    this.initResumes()
    this.initPositions()
    this.initHrs()
    if (this.$route.query.resumeId) {
      this.interview.resumeId = parseInt(this.$route.query.resumeId)
      this.showAddInterviewView()
    }
  },
  methods: {
    initInterviews() {
      this.loading = true
      let params = {
        page: this.page,
        size: this.size,
        interviewType: this.searchValue.interviewType,
        interviewStatus: this.searchValue.interviewStatus,
        interviewResult: this.searchValue.interviewResult
      }
      if (this.searchValue.interviewTimeScope) {
        params.interviewTimeScope = this.searchValue.interviewTimeScope.join(',')
      }
      getInterviewByPage(params).then(resp => {
        this.loading = false
        if (resp) {
          this.interviews = resp.data
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
    initHrs() {
      getRequest('/system/hr/').then(resp => {
        if (resp) {
          this.hrs = resp
        }
      })
    },
    currentChange(page) {
      this.page = page
      this.initInterviews()
    },
    sizeChange(size) {
      this.size = size
      this.initInterviews()
    },
    showAddInterviewView() {
      this.title = '安排面试'
      this.interview = {
        resumeId: this.$route.query.resumeId ? parseInt(this.$route.query.resumeId) : null,
        posId: null,
        interviewType: '初面',
        interviewTime: '',
        interviewAddress: '',
        interviewerId: null,
        interviewerName: '',
        remark: ''
      }
      this.dialogVisible = true
    },
    showEditInterviewView(data) {
      this.title = '编辑面试'
      this.interview = Object.assign({}, data)
      this.dialogVisible = true
    },
    handleInterviewerChange(val) {
      let hr = this.hrs.find(h => h.id === val)
      if (hr) {
        this.interview.interviewerName = hr.name
      }
    },
    doAddInterview() {
      if (this.interview.id) {
        updateInterview(this.interview).then(resp => {
          if (resp) {
            this.dialogVisible = false
            this.initInterviews()
          }
        })
      } else {
        addInterview(this.interview).then(resp => {
          if (resp) {
            this.dialogVisible = false
            this.initInterviews()
          }
        })
      }
    },
    deleteInterview(data) {
      this.$confirm('此操作将删除该面试记录, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteInterview(data.id).then(resp => {
          if (resp) {
            this.initInterviews()
          }
        })
      }).catch(() => {})
    },
    showResultView(data) {
      this.resultForm = {
        id: data.id,
        interviewResult: '',
        score: 5,
        evaluation: '',
        advantage: '',
        disadvantage: '',
        hireSuggestion: ''
      }
      this.resultDialogVisible = true
    },
    doSubmitResult() {
      updateInterviewResult(this.resultForm).then(resp => {
        if (resp) {
          this.resultDialogVisible = false
          this.initInterviews()
        }
      })
    },
    sendNotify(data) {
      updateNotifyStatus(data.id).then(resp => {
        if (resp) {
          this.initInterviews()
        }
      })
    },
    clearSearchValue() {
      this.searchValue = {
        interviewType: '',
        interviewStatus: '',
        interviewResult: '',
        interviewTimeScope: null
      }
      this.searchName = ''
      this.initInterviews()
    },
    getInterviewStatusType(status) {
      switch (status) {
        case '待面试': return 'warning'
        case '已面试': return 'success'
        case '已取消': return 'info'
        default: return 'info'
      }
    },
    getResultType(result) {
      switch (result) {
        case '通过': return 'success'
        case '未通过': return 'danger'
        case '待定': return 'warning'
        default: return 'info'
      }
    },
    showStatistics() {
      getInterviewStatistics({}).then(resp => {
        if (resp) {
          this.statistics = resp
          this.statisticsDialogVisible = true
        }
      })
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
