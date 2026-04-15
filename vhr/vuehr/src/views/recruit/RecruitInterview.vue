<template>
    <div>
        <div style="display: flex;justify-content: space-between;margin-bottom: 10px;">
            <div>
                <el-input placeholder="请输入候选人姓名搜索..." prefix-icon="el-icon-search"
                          clearable
                          @clear="initInterviews"
                          style="width: 300px;margin-right: 10px" v-model="keyword"
                          @keydown.enter.native="initInterviews"></el-input>
                <el-button icon="el-icon-search" type="primary" @click="initInterviews">搜索</el-button>
            </div>
            <div>
                <el-button type="primary" icon="el-icon-plus" @click="showAddInterviewView">安排面试</el-button>
                <el-button type="success" icon="el-icon-s-data" @click="showStatisticsView">面试统计</el-button>
            </div>
        </div>

        <el-table
                :data="interviews"
                stripe
                border
                v-loading="loading"
                element-loading-text="正在加载..."
                element-loading-spinner="el-icon-loading"
                element-loading-background="rgba(0, 0, 0, 0.8)"
                style="width: 100%">
            <el-table-column type="selection" width="55"></el-table-column>
            <el-table-column prop="id" label="编号" width="70"></el-table-column>
            <el-table-column prop="candidateName" label="候选人" width="100"></el-table-column>
            <el-table-column prop="positionName" label="面试岗位" width="120"></el-table-column>
            <el-table-column prop="interviewType" label="面试类型" width="90">
                <template slot-scope="scope">
                    <el-tag :type="getInterviewTypeType(scope.row.interviewType)">
                        {{getInterviewTypeText(scope.row.interviewType)}}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="interviewTime" label="面试时间" width="150"></el-table-column>
            <el-table-column prop="interviewLocation" label="面试地点" width="150"></el-table-column>
            <el-table-column prop="interviewerName" label="面试官" width="100"></el-table-column>
            <el-table-column prop="status" label="状态" width="90">
                <template slot-scope="scope">
                    <el-tag :type="getStatusType(scope.row.status)">{{getStatusText(scope.row.status)}}</el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="score" label="评分" width="70">
                <template slot-scope="scope">
                    <span v-if="scope.row.score">{{scope.row.score}}分</span>
                    <span v-else>-</span>
                </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="150"></el-table-column>
            <el-table-column label="操作" fixed="right" width="280">
                <template slot-scope="scope">
                    <el-button size="mini" @click="showEditInterviewView(scope.row)">编辑</el-button>
                    <el-button size="mini" type="primary" @click="showEvaluationView(scope.row)"
                               :disabled="scope.row.status !== 0">评价</el-button>
                    <el-button size="mini" type="success" @click="handleCreateOffer(scope.row)"
                               :disabled="scope.row.status !== 1">创建Offer</el-button>
                    <el-button size="mini" type="danger" @click="deleteInterview(scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <div style="display: flex;justify-content: flex-end;margin-top: 10px;">
            <el-pagination
                    background
                    @current-change="currentChange"
                    @size-change="sizeChange"
                    layout="sizes, prev, pager, next, jumper, ->, total, slot"
                    :total="total">
            </el-pagination>
        </div>

        <el-dialog :title="title" :visible.sync="dialogVisible" width="60%">
            <el-form :model="interview" :rules="rules" ref="interviewForm" label-width="100px">
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="候选人:" prop="resumeId">
                            <el-select v-model="interview.resumeId" placeholder="请选择候选人" style="width: 100%;"
                                       @change="handleResumeChange" :disabled="!!$route.query.resumeId">
                                <el-option
                                        v-for="item in resumeList"
                                        :key="item.id"
                                        :label="item.name"
                                        :value="item.id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="面试岗位:" prop="positionId">
                            <el-select v-model="interview.positionId" placeholder="请选择岗位" style="width: 100%;"
                                       @change="handlePositionChange">
                                <el-option
                                        v-for="item in positionList"
                                        :key="item.id"
                                        :label="item.name"
                                        :value="item.id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="面试时间:" prop="interviewTime">
                            <el-date-picker
                                    v-model="interview.interviewTime"
                                    type="datetime"
                                    placeholder="选择日期时间"
                                    style="width: 100%;">
                            </el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="面试地点:" prop="interviewLocation">
                            <el-input v-model="interview.interviewLocation" placeholder="请输入面试地点"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="面试官:" prop="interviewerId">
                            <el-select v-model="interview.interviewerId" placeholder="请选择面试官" style="width: 100%;"
                                       @change="handleInterviewerChange">
                                <el-option
                                        v-for="item in hrList"
                                        :key="item.id"
                                        :label="item.name"
                                        :value="item.id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="面试类型:" prop="interviewType">
                            <el-select v-model="interview.interviewType" placeholder="请选择面试类型" style="width: 100%;">
                                <el-option label="初面" :value="1"></el-option>
                                <el-option label="复面" :value="2"></el-option>
                                <el-option label="终面" :value="3"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="备注:">
                            <el-input type="textarea" v-model="interview.remark" :rows="3"
                                      placeholder="请输入备注"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="doAddInterview">确 定</el-button>
            </span>
        </el-dialog>

        <el-dialog title="面试评价" :visible.sync="evaluationVisible" width="60%">
            <el-form :model="evaluation" :rules="evaluationRules" ref="evaluationForm" label-width="100px">
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="面试评分:" prop="score">
                            <el-slider v-model="evaluation.score" :max="100" show-input></el-slider>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="录用建议:" prop="suggestion">
                            <el-radio-group v-model="evaluation.suggestion">
                                <el-radio :label="1">建议录用</el-radio>
                                <el-radio :label="2">不建议录用</el-radio>
                                <el-radio :label="0">待定</el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="优点:">
                            <el-input type="textarea" v-model="evaluation.advantages" :rows="3"
                                      placeholder="请输入优点评价"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="缺点:">
                            <el-input type="textarea" v-model="evaluation.disadvantages" :rows="3"
                                      placeholder="请输入缺点评价"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="综合评价:">
                            <el-input type="textarea" v-model="evaluation.evaluation" :rows="4"
                                      placeholder="请输入综合评价"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="面试结果:" prop="status">
                            <el-radio-group v-model="evaluation.status">
                                <el-radio :label="1">通过</el-radio>
                                <el-radio :label="2">未通过</el-radio>
                                <el-radio :label="3">待定</el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="evaluationVisible = false">取 消</el-button>
                <el-button type="primary" @click="doSubmitEvaluation">提 交</el-button>
            </span>
        </el-dialog>

        <el-dialog title="面试统计" :visible.sync="statisticsVisible" width="70%">
            <el-row style="margin-bottom: 20px;">
                <el-col :span="12">
                    <span>时间范围：</span>
                    <el-date-picker
                            v-model="statisticsDateRange"
                            type="daterange"
                            range-separator="至"
                            start-placeholder="开始日期"
                            end-placeholder="结束日期"
                            value-format="yyyy-MM-dd">
                    </el-date-picker>
                </el-col>
                <el-col :span="12" style="text-align: right;">
                    <el-button type="primary" @click="loadStatistics">查询</el-button>
                </el-col>
            </el-row>
            <el-table :data="statisticsData" stripe border style="width: 100%">
                <el-table-column prop="positionName" label="岗位名称"></el-table-column>
                <el-table-column prop="totalCount" label="面试人数"></el-table-column>
                <el-table-column prop="passCount" label="通过人数"></el-table-column>
                <el-table-column prop="failCount" label="未通过人数"></el-table-column>
                <el-table-column label="通过率">
                    <template slot-scope="scope">
                        {{scope.row.totalCount > 0 ? ((scope.row.passCount / scope.row.totalCount) * 100).toFixed(2) : 0}}%
                    </template>
                </el-table-column>
            </el-table>
        </el-dialog>
    </div>
</template>

<script>
    import {
        getInterviewList, addInterview, updateInterview, deleteInterview,
        updateInterviewEvaluation, updateInterviewStatus, getInterviewStatistics
    } from '@/api/recruit';
    import {getResumeByStatus} from '@/api/recruit';

    export default {
        name: "RecruitInterview",
        data() {
            return {
                interviews: [],
                loading: false,
                total: 0,
                page: 1,
                size: 10,
                keyword: '',
                dialogVisible: false,
                evaluationVisible: false,
                statisticsVisible: false,
                title: '',
                interview: {
                    id: null,
                    resumeId: null,
                    candidateName: '',
                    positionId: null,
                    positionName: '',
                    interviewTime: '',
                    interviewLocation: '',
                    interviewerId: null,
                    interviewerName: '',
                    interviewType: 1,
                    remark: ''
                },
                evaluation: {
                    id: null,
                    score: 80,
                    advantages: '',
                    disadvantages: '',
                    suggestion: 0,
                    evaluation: '',
                    status: 1
                },
                resumeList: [],
                positionList: [],
                hrList: [],
                statisticsData: [],
                statisticsDateRange: [],
                rules: {
                    resumeId: [{required: true, message: '请选择候选人', trigger: 'change'}],
                    positionId: [{required: true, message: '请选择岗位', trigger: 'change'}],
                    interviewTime: [{required: true, message: '请选择面试时间', trigger: 'change'}],
                    interviewLocation: [{required: true, message: '请输入面试地点', trigger: 'blur'}],
                    interviewerId: [{required: true, message: '请选择面试官', trigger: 'change'}],
                    interviewType: [{required: true, message: '请选择面试类型', trigger: 'change'}]
                },
                evaluationRules: {
                    score: [{required: true, message: '请评分', trigger: 'blur'}],
                    suggestion: [{required: true, message: '请选择录用建议', trigger: 'change'}],
                    status: [{required: true, message: '请选择面试结果', trigger: 'change'}]
                }
            }
        },
        mounted() {
            this.initInterviews();
            this.loadResumes();
            this.loadPositions();
            this.loadHrs();
            if (this.$route.query.resumeId) {
                this.showAddInterviewView();
                this.interview.resumeId = parseInt(this.$route.query.resumeId);
                this.interview.candidateName = this.$route.query.candidateName;
            }
        },
        methods: {
            initInterviews() {
                this.loading = true;
                let url = '/recruit/interview/?page=' + this.page + '&size=' + this.size;
                if (this.keyword) {
                    url += '&candidateName=' + this.keyword;
                }
                this.getRequest(url).then(resp => {
                    this.loading = false;
                    if (resp) {
                        this.interviews = resp.data;
                        this.total = resp.total;
                    }
                });
            },
            currentChange(currentPage) {
                this.page = currentPage;
                this.initInterviews();
            },
            sizeChange(currentSize) {
                this.size = currentSize;
                this.initInterviews();
            },
            loadResumes() {
                getResumeByStatus(1).then(resp => {
                    if (resp) {
                        this.resumeList = resp;
                    }
                });
            },
            loadPositions() {
                this.getRequest('/system/basic/positions/').then(resp => {
                    if (resp) {
                        this.positionList = resp;
                    }
                });
            },
            loadHrs() {
                this.getRequest('/system/hr/').then(resp => {
                    if (resp) {
                        this.hrList = resp.obj;
                    }
                });
            },
            showAddInterviewView() {
                this.title = '安排面试';
                this.interview = {
                    id: null,
                    resumeId: null,
                    candidateName: '',
                    positionId: null,
                    positionName: '',
                    interviewTime: '',
                    interviewLocation: '',
                    interviewerId: null,
                    interviewerName: '',
                    interviewType: 1,
                    remark: ''
                };
                this.dialogVisible = true;
            },
            showEditInterviewView(data) {
                this.title = '编辑面试';
                this.interview = JSON.parse(JSON.stringify(data));
                this.dialogVisible = true;
            },
            showEvaluationView(data) {
                this.evaluation = {
                    id: data.id,
                    score: data.score || 80,
                    advantages: data.advantages || '',
                    disadvantages: data.disadvantages || '',
                    suggestion: data.suggestion || 0,
                    evaluation: data.evaluation || '',
                    status: 1
                };
                this.evaluationVisible = true;
            },
            showStatisticsView() {
                this.statisticsVisible = true;
                const end = new Date();
                const start = new Date();
                start.setMonth(start.getMonth() - 1);
                this.statisticsDateRange = [this.formatDate(start), this.formatDate(end)];
                this.loadStatistics();
            },
            doAddInterview() {
                this.$refs.interviewForm.validate(valid => {
                    if (valid) {
                        if (this.interview.id) {
                            updateInterview(this.interview).then(resp => {
                                if (resp.status === 200) {
                                    this.dialogVisible = false;
                                    this.initInterviews();
                                }
                            });
                        } else {
                            addInterview(this.interview).then(resp => {
                                if (resp.status === 200) {
                                    this.dialogVisible = false;
                                    this.initInterviews();
                                }
                            });
                        }
                    }
                });
            },
            doSubmitEvaluation() {
                this.$refs.evaluationForm.validate(valid => {
                    if (valid) {
                        updateInterviewEvaluation(this.evaluation.id, this.evaluation).then(resp => {
                            if (resp.status === 200) {
                                updateInterviewStatus(this.evaluation.id, this.evaluation.status).then(resp2 => {
                                    if (resp2.status === 200) {
                                        this.evaluationVisible = false;
                                        this.initInterviews();
                                    }
                                });
                            }
                        });
                    }
                });
            },
            deleteInterview(data) {
                this.$confirm('此操作将永久删除该面试安排, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    deleteInterview(data.id).then(resp => {
                        if (resp.status === 200) {
                            this.initInterviews();
                        }
                    });
                }).catch(() => {
                    this.$message.info('已取消删除');
                });
            },
            handleCreateOffer(data) {
                this.$router.push({
                    path: '/recruit/offer',
                    query: {
                        resumeId: data.resumeId,
                        interviewId: data.id,
                        candidateName: data.candidateName,
                        positionId: data.positionId,
                        positionName: data.positionName
                    }
                });
            },
            handleResumeChange(val) {
                const resume = this.resumeList.find(item => item.id === val);
                if (resume) {
                    this.interview.candidateName = resume.name;
                }
            },
            handlePositionChange(val) {
                const position = this.positionList.find(item => item.id === val);
                if (position) {
                    this.interview.positionName = position.name;
                }
            },
            handleInterviewerChange(val) {
                const hr = this.hrList.find(item => item.id === val);
                if (hr) {
                    this.interview.interviewerName = hr.name;
                }
            },
            loadStatistics() {
                if (this.statisticsDateRange && this.statisticsDateRange.length === 2) {
                    getInterviewStatistics(this.statisticsDateRange[0], this.statisticsDateRange[1]).then(resp => {
                        if (resp) {
                            this.statisticsData = resp;
                        }
                    });
                }
            },
            formatDate(date) {
                const year = date.getFullYear();
                const month = String(date.getMonth() + 1).padStart(2, '0');
                const day = String(date.getDate()).padStart(2, '0');
                return `${year}-${month}-${day}`;
            },
            getInterviewTypeType(type) {
                const types = ['', 'primary', 'warning', 'danger'];
                return types[type] || 'info';
            },
            getInterviewTypeText(type) {
                const texts = ['', '初面', '复面', '终面'];
                return texts[type] || '未知';
            },
            getStatusType(status) {
                const types = ['info', 'success', 'danger', 'warning'];
                return types[status] || 'info';
            },
            getStatusText(status) {
                const texts = ['待面试', '已通过', '未通过', '待定'];
                return texts[status] || '未知';
            }
        }
    }
</script>
