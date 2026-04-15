<template>
    <div>
        <div style="display: flex;justify-content: space-between;margin-bottom: 10px;">
            <div>
                <el-input placeholder="请输入候选人姓名搜索..." prefix-icon="el-icon-search"
                          clearable
                          @clear="initOffers"
                          style="width: 300px;margin-right: 10px" v-model="keyword"
                          @keydown.enter.native="initOffers"></el-input>
                <el-button icon="el-icon-search" type="primary" @click="initOffers">搜索</el-button>
            </div>
            <div>
                <el-button type="primary" icon="el-icon-plus" @click="showAddOfferView">创建Offer</el-button>
            </div>
        </div>

        <el-table
                :data="offers"
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
            <el-table-column prop="positionName" label="岗位" width="120"></el-table-column>
            <el-table-column prop="salary" label="薪资" width="100"></el-table-column>
            <el-table-column prop="probationPeriod" label="试用期" width="80">
                <template slot-scope="scope">
                    {{scope.row.probationPeriod}}个月
                </template>
            </el-table-column>
            <el-table-column prop="entryDate" label="入职日期" width="110"></el-table-column>
            <el-table-column prop="offerValidDate" label="有效期至" width="110"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
                <template slot-scope="scope">
                    <el-tag :type="getStatusType(scope.row.status)">{{getStatusText(scope.row.status)}}</el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="viewStatus" label="查看状态" width="90">
                <template slot-scope="scope">
                    <el-tag :type="scope.row.viewStatus === 1 ? 'success' : 'info'" size="small">
                        {{scope.row.viewStatus === 1 ? '已查看' : '未查看'}}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="sendTime" label="发送时间" width="150"></el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="150"></el-table-column>
            <el-table-column label="操作" fixed="right" width="280">
                <template slot-scope="scope">
                    <el-button size="mini" @click="showEditOfferView(scope.row)">编辑</el-button>
                    <el-button size="mini" type="primary" @click="handleSendOffer(scope.row)"
                               :disabled="scope.row.status !== 0">发送</el-button>
                    <el-button size="mini" type="success" @click="handleAcceptOffer(scope.row)"
                               :disabled="scope.row.status !== 1">接受</el-button>
                    <el-button size="mini" type="danger" @click="deleteOffer(scope.row)">删除</el-button>
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
            <el-form :model="offer" :rules="rules" ref="offerForm" label-width="100px">
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="候选人:" prop="resumeId">
                            <el-select v-model="offer.resumeId" placeholder="请选择候选人" style="width: 100%;"
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
                        <el-form-item label="岗位:" prop="positionId">
                            <el-select v-model="offer.positionId" placeholder="请选择岗位" style="width: 100%;"
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
                        <el-form-item label="薪资:" prop="salary">
                            <el-input v-model="offer.salary" placeholder="请输入薪资"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="试用期:" prop="probationPeriod">
                            <el-select v-model="offer.probationPeriod" placeholder="请选择试用期" style="width: 100%;">
                                <el-option label="1个月" :value="1"></el-option>
                                <el-option label="2个月" :value="2"></el-option>
                                <el-option label="3个月" :value="3"></el-option>
                                <el-option label="6个月" :value="6"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="入职日期:" prop="entryDate">
                            <el-date-picker
                                    v-model="offer.entryDate"
                                    type="date"
                                    placeholder="选择入职日期"
                                    style="width: 100%;">
                            </el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="有效期至:" prop="offerValidDate">
                            <el-date-picker
                                    v-model="offer.offerValidDate"
                                    type="date"
                                    placeholder="选择有效期"
                                    style="width: 100%;">
                            </el-date-picker>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="备注:">
                            <el-input type="textarea" v-model="offer.remark" :rows="3"
                                      placeholder="请输入备注"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="doAddOffer">确 定</el-button>
            </span>
        </el-dialog>

        <el-dialog title="拒绝Offer" :visible.sync="rejectVisible" width="40%">
            <el-form :model="rejectForm" label-width="100px">
                <el-form-item label="拒绝原因:">
                    <el-input type="textarea" v-model="rejectForm.reason" :rows="4"
                              placeholder="请输入拒绝原因"></el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="rejectVisible = false">取 消</el-button>
                <el-button type="primary" @click="doRejectOffer">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    import {
        getOfferList, addOffer, updateOffer, deleteOffer,
        sendOffer, updateOfferStatus
    } from '@/api/recruit';
    import {getResumeByStatus} from '@/api/recruit';

    export default {
        name: "RecruitOffer",
        data() {
            return {
                offers: [],
                loading: false,
                total: 0,
                page: 1,
                size: 10,
                keyword: '',
                dialogVisible: false,
                rejectVisible: false,
                title: '',
                offer: {
                    id: null,
                    resumeId: null,
                    interviewId: null,
                    candidateName: '',
                    positionId: null,
                    positionName: '',
                    salary: '',
                    probationPeriod: 3,
                    entryDate: '',
                    offerValidDate: '',
                    remark: ''
                },
                rejectForm: {
                    id: null,
                    reason: ''
                },
                resumeList: [],
                positionList: [],
                rules: {
                    resumeId: [{required: true, message: '请选择候选人', trigger: 'change'}],
                    positionId: [{required: true, message: '请选择岗位', trigger: 'change'}],
                    salary: [{required: true, message: '请输入薪资', trigger: 'blur'}],
                    probationPeriod: [{required: true, message: '请选择试用期', trigger: 'change'}],
                    entryDate: [{required: true, message: '请选择入职日期', trigger: 'change'}],
                    offerValidDate: [{required: true, message: '请选择有效期', trigger: 'change'}]
                }
            }
        },
        mounted() {
            this.initOffers();
            this.loadResumes();
            this.loadPositions();
            if (this.$route.query.resumeId) {
                this.showAddOfferView();
                this.offer.resumeId = parseInt(this.$route.query.resumeId);
                this.offer.candidateName = this.$route.query.candidateName;
                if (this.$route.query.interviewId) {
                    this.offer.interviewId = parseInt(this.$route.query.interviewId);
                }
                if (this.$route.query.positionId) {
                    this.offer.positionId = parseInt(this.$route.query.positionId);
                    this.offer.positionName = this.$route.query.positionName;
                }
            }
        },
        methods: {
            initOffers() {
                this.loading = true;
                let url = '/recruit/offer/?page=' + this.page + '&size=' + this.size;
                if (this.keyword) {
                    url += '&candidateName=' + this.keyword;
                }
                this.getRequest(url).then(resp => {
                    this.loading = false;
                    if (resp) {
                        this.offers = resp.data;
                        this.total = resp.total;
                    }
                });
            },
            currentChange(currentPage) {
                this.page = currentPage;
                this.initOffers();
            },
            sizeChange(currentSize) {
                this.size = currentSize;
                this.initOffers();
            },
            loadResumes() {
                getResumeByStatus(4).then(resp => {
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
            showAddOfferView() {
                this.title = '创建Offer';
                this.offer = {
                    id: null,
                    resumeId: null,
                    interviewId: null,
                    candidateName: '',
                    positionId: null,
                    positionName: '',
                    salary: '',
                    probationPeriod: 3,
                    entryDate: '',
                    offerValidDate: '',
                    remark: ''
                };
                this.dialogVisible = true;
            },
            showEditOfferView(data) {
                this.title = '编辑Offer';
                this.offer = JSON.parse(JSON.stringify(data));
                this.dialogVisible = true;
            },
            doAddOffer() {
                this.$refs.offerForm.validate(valid => {
                    if (valid) {
                        if (this.offer.id) {
                            updateOffer(this.offer).then(resp => {
                                if (resp.status === 200) {
                                    this.dialogVisible = false;
                                    this.initOffers();
                                }
                            });
                        } else {
                            addOffer(this.offer).then(resp => {
                                if (resp.status === 200) {
                                    this.dialogVisible = false;
                                    this.initOffers();
                                }
                            });
                        }
                    }
                });
            },
            deleteOffer(data) {
                this.$confirm('此操作将永久删除该Offer, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    deleteOffer(data.id).then(resp => {
                        if (resp.status === 200) {
                            this.initOffers();
                        }
                    });
                }).catch(() => {
                    this.$message.info('已取消删除');
                });
            },
            handleSendOffer(data) {
                this.$confirm('确认发送Offer给候选人?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    sendOffer(data.id).then(resp => {
                        if (resp.status === 200) {
                            this.initOffers();
                        }
                    });
                }).catch(() => {
                    this.$message.info('已取消发送');
                });
            },
            handleAcceptOffer(data) {
                this.$confirm('确认候选人已接受Offer?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    updateOfferStatus(data.id, 2).then(resp => {
                        if (resp.status === 200) {
                            this.$message.success('Offer已接受，请准备入职事宜');
                            this.initOffers();
                        }
                    });
                }).catch(() => {
                    this.$message.info('已取消');
                });
            },
            doRejectOffer() {
                updateOfferStatus(this.rejectForm.id, 3, this.rejectForm.reason).then(resp => {
                    if (resp.status === 200) {
                        this.rejectVisible = false;
                        this.initOffers();
                    }
                });
            },
            handleResumeChange(val) {
                const resume = this.resumeList.find(item => item.id === val);
                if (resume) {
                    this.offer.candidateName = resume.name;
                }
            },
            handlePositionChange(val) {
                const position = this.positionList.find(item => item.id === val);
                if (position) {
                    this.offer.positionName = position.name;
                }
            },
            getStatusType(status) {
                const types = ['info', 'primary', 'success', 'danger', 'warning'];
                return types[status] || 'info';
            },
            getStatusText(status) {
                const texts = ['待确认', '已发送', '已接受', '已拒绝', '已过期'];
                return texts[status] || '未知';
            }
        }
    }
</script>
