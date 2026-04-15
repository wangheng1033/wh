<template>
    <div>
        <div style="display: flex;justify-content: space-between;margin-bottom: 10px;">
            <div>
                <el-input placeholder="请输入姓名或电话搜索..." prefix-icon="el-icon-search"
                          clearable
                          @clear="initResumes"
                          style="width: 300px;margin-right: 10px" v-model="keyword"
                          @keydown.enter.native="initResumes"></el-input>
                <el-button icon="el-icon-search" type="primary" @click="initResumes">搜索</el-button>
                <el-button type="primary" @click="showAdvanceSearchView = !showAdvanceSearchView">
                    <i :class="showAdvanceSearchView?'fa fa-angle-double-up':'fa fa-angle-double-down'"
                       aria-hidden="true"></i>
                    高级搜索
                </el-button>
            </div>
            <div>
                <el-upload
                        :show-file-list="false"
                        :before-upload="beforeUpload"
                        :on-success="onSuccess"
                        :on-error="onError"
                        :disabled="importDataDisabled"
                        style="display: inline-flex;margin-right: 8px"
                        action="/recruit/resume/import">
                    <el-button :disabled="importDataDisabled" type="success" :icon="importDataBtnIcon">
                        {{importDataBtnText}}
                    </el-button>
                </el-upload>
                <el-button type="success" @click="exportData" icon="el-icon-download">
                    导出数据
                </el-button>
                <el-button type="primary" icon="el-icon-plus" @click="showAddResumeView">
                    添加简历
                </el-button>
            </div>
        </div>

        <transition name="slide-fade">
            <div v-show="showAdvanceSearchView"
                 style="border: 1px solid #409eff;border-radius: 5px;box-sizing: border-box;padding: 5px;margin: 10px 0px;">
                <el-row>
                    <el-col :span="6">
                        学历:
                        <el-select v-model="searchValue.education" placeholder="学历" size="mini" style="width: 150px;">
                            <el-option label="全部" value=""></el-option>
                            <el-option label="博士" value="博士"></el-option>
                            <el-option label="硕士" value="硕士"></el-option>
                            <el-option label="本科" value="本科"></el-option>
                            <el-option label="大专" value="大专"></el-option>
                            <el-option label="高中" value="高中"></el-option>
                            <el-option label="其他" value="其他"></el-option>
                        </el-select>
                    </el-col>
                    <el-col :span="6">
                        状态:
                        <el-select v-model="searchValue.status" placeholder="状态" size="mini" style="width: 150px;">
                            <el-option label="全部" :value="null"></el-option>
                            <el-option label="待筛选" :value="0"></el-option>
                            <el-option label="已筛选" :value="1"></el-option>
                            <el-option label="待面试" :value="2"></el-option>
                            <el-option label="已拒绝" :value="3"></el-option>
                            <el-option label="已录用" :value="4"></el-option>
                        </el-select>
                    </el-col>
                    <el-col :span="6">
                        工作年限:
                        <el-input-number v-model="searchValue.workYears" :min="0" :max="50" size="mini"
                                         style="width: 120px;"></el-input-number>
                    </el-col>
                    <el-col :span="6">
                        <el-button size="mini" @click="resetSearch">取消</el-button>
                        <el-button size="mini" icon="el-icon-search" type="primary" @click="initResumes('advanced')">搜索</el-button>
                    </el-col>
                </el-row>
            </div>
        </transition>

        <el-table
                :data="resumes"
                stripe
                border
                v-loading="loading"
                element-loading-text="正在加载..."
                element-loading-spinner="el-icon-loading"
                element-loading-background="rgba(0, 0, 0, 0.8)"
                style="width: 100%">
            <el-table-column type="selection" width="55"></el-table-column>
            <el-table-column prop="id" label="编号" width="70"></el-table-column>
            <el-table-column prop="name" label="姓名" width="100"></el-table-column>
            <el-table-column prop="gender" label="性别" width="70"></el-table-column>
            <el-table-column prop="age" label="年龄" width="70"></el-table-column>
            <el-table-column prop="education" label="学历" width="100"></el-table-column>
            <el-table-column prop="workYears" label="工作年限" width="90">
                <template slot-scope="scope">
                    {{scope.row.workYears}}年
                </template>
            </el-table-column>
            <el-table-column prop="phone" label="联系电话" width="120"></el-table-column>
            <el-table-column prop="email" label="邮箱" width="150"></el-table-column>
            <el-table-column prop="expectedSalary" label="期望薪资" width="100"></el-table-column>
            <el-table-column prop="jobIntention" label="求职意向" width="120"></el-table-column>
            <el-table-column prop="status" label="状态" width="90">
                <template slot-scope="scope">
                    <el-tag :type="getStatusType(scope.row.status)">{{getStatusText(scope.row.status)}}</el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="150"></el-table-column>
            <el-table-column label="操作" fixed="right" width="280">
                <template slot-scope="scope">
                    <el-button size="mini" @click="showEditResumeView(scope.row)">编辑</el-button>
                    <el-button size="mini" type="primary" @click="showDetailView(scope.row)">详情</el-button>
                    <el-button size="mini" type="success" @click="handleArrangeInterview(scope.row)"
                               :disabled="scope.row.status !== 1">安排面试</el-button>
                    <el-button size="mini" type="danger" @click="deleteResume(scope.row)">删除</el-button>
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
            <el-form :model="resume" :rules="rules" ref="resumeForm" label-width="100px">
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="姓名:" prop="name">
                            <el-input v-model="resume.name" placeholder="请输入姓名"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="性别:" prop="gender">
                            <el-radio-group v-model="resume.gender">
                                <el-radio label="男">男</el-radio>
                                <el-radio label="女">女</el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="年龄:" prop="age">
                            <el-input-number v-model="resume.age" :min="18" :max="65" style="width: 100%;"></el-input-number>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="学历:" prop="education">
                            <el-select v-model="resume.education" placeholder="请选择学历" style="width: 100%;">
                                <el-option label="博士" value="博士"></el-option>
                                <el-option label="硕士" value="硕士"></el-option>
                                <el-option label="本科" value="本科"></el-option>
                                <el-option label="大专" value="大专"></el-option>
                                <el-option label="高中" value="高中"></el-option>
                                <el-option label="其他" value="其他"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="工作年限:" prop="workYears">
                            <el-input-number v-model="resume.workYears" :min="0" :max="50" style="width: 100%;"></el-input-number>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="联系电话:" prop="phone">
                            <el-input v-model="resume.phone" placeholder="请输入联系电话"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="邮箱:" prop="email">
                            <el-input v-model="resume.email" placeholder="请输入邮箱"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="期望薪资:" prop="expectedSalary">
                            <el-input v-model="resume.expectedSalary" placeholder="请输入期望薪资"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="求职意向:" prop="jobIntention">
                            <el-input v-model="resume.jobIntention" placeholder="请输入求职意向"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="工作经历:">
                            <el-input type="textarea" v-model="resume.workExperience" :rows="4"
                                      placeholder="请输入工作经历"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="教育经历:">
                            <el-input type="textarea" v-model="resume.educationExperience" :rows="4"
                                      placeholder="请输入教育经历"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="简历附件:">
                            <el-upload
                                    class="upload-demo"
                                    action="/file/upload"
                                    :on-success="handleUploadSuccess"
                                    :on-remove="handleRemove"
                                    :file-list="fileList">
                                <el-button size="small" type="primary">点击上传</el-button>
                                <div slot="tip" class="el-upload__tip">支持PDF/Word格式文件</div>
                            </el-upload>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="备注:">
                            <el-input type="textarea" v-model="resume.remark" :rows="2"
                                      placeholder="请输入备注"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="doAddResume">确 定</el-button>
            </span>
        </el-dialog>

        <el-dialog title="简历详情" :visible.sync="detailVisible" width="60%">
            <el-descriptions :column="2" border>
                <el-descriptions-item label="姓名">{{currentResume.name}}</el-descriptions-item>
                <el-descriptions-item label="性别">{{currentResume.gender}}</el-descriptions-item>
                <el-descriptions-item label="年龄">{{currentResume.age}}岁</el-descriptions-item>
                <el-descriptions-item label="学历">{{currentResume.education}}</el-descriptions-item>
                <el-descriptions-item label="工作年限">{{currentResume.workYears}}年</el-descriptions-item>
                <el-descriptions-item label="联系电话">{{currentResume.phone}}</el-descriptions-item>
                <el-descriptions-item label="邮箱">{{currentResume.email}}</el-descriptions-item>
                <el-descriptions-item label="期望薪资">{{currentResume.expectedSalary}}</el-descriptions-item>
                <el-descriptions-item label="求职意向" :span="2">{{currentResume.jobIntention}}</el-descriptions-item>
                <el-descriptions-item label="状态">
                    <el-tag :type="getStatusType(currentResume.status)">{{getStatusText(currentResume.status)}}</el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="创建时间">{{currentResume.createTime}}</el-descriptions-item>
            </el-descriptions>
            <div style="margin-top: 20px;">
                <h4>工作经历</h4>
                <div style="padding: 10px;background: #f5f7fa;border-radius: 4px;">
                    {{currentResume.workExperience || '暂无'}}
                </div>
            </div>
            <div style="margin-top: 20px;">
                <h4>教育经历</h4>
                <div style="padding: 10px;background: #f5f7fa;border-radius: 4px;">
                    {{currentResume.educationExperience || '暂无'}}
                </div>
            </div>
            <div style="margin-top: 20px;" v-if="currentResume.attachmentUrl">
                <h4>简历附件</h4>
                <el-link type="primary" :href="currentResume.attachmentUrl" target="_blank">
                    {{currentResume.attachmentName}}
                </el-link>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button @click="detailVisible = false">关 闭</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    import {
        getResumeList, addResume, updateResume, deleteResume,
        exportResume, importResume
    } from '@/api/recruit';

    export default {
        name: "RecruitResume",
        data() {
            return {
                resumes: [],
                loading: false,
                total: 0,
                page: 1,
                size: 10,
                keyword: '',
                showAdvanceSearchView: false,
                searchValue: {
                    education: '',
                    status: null,
                    workYears: null
                },
                dialogVisible: false,
                detailVisible: false,
                title: '',
                resume: {
                    id: null,
                    name: '',
                    gender: '男',
                    age: 25,
                    education: '',
                    workYears: 0,
                    phone: '',
                    email: '',
                    expectedSalary: '',
                    jobIntention: '',
                    workExperience: '',
                    educationExperience: '',
                    attachmentUrl: '',
                    attachmentName: '',
                    remark: ''
                },
                currentResume: {},
                fileList: [],
                importDataBtnText: '导入数据',
                importDataBtnIcon: 'el-icon-upload2',
                importDataDisabled: false,
                rules: {
                    name: [{required: true, message: '请输入姓名', trigger: 'blur'}],
                    gender: [{required: true, message: '请选择性别', trigger: 'change'}],
                    age: [{required: true, message: '请输入年龄', trigger: 'blur'}],
                    education: [{required: true, message: '请选择学历', trigger: 'change'}],
                    phone: [{required: true, message: '请输入联系电话', trigger: 'blur'}],
                    jobIntention: [{required: true, message: '请输入求职意向', trigger: 'blur'}]
                }
            }
        },
        mounted() {
            this.initResumes();
        },
        methods: {
            initResumes(type) {
                this.loading = true;
                let url = '/recruit/resume/?page=' + this.page + '&size=' + this.size;
                if (type && type === 'advanced') {
                    if (this.searchValue.education) {
                        url += '&education=' + this.searchValue.education;
                    }
                    if (this.searchValue.status !== null) {
                        url += '&status=' + this.searchValue.status;
                    }
                    if (this.searchValue.workYears !== null) {
                        url += '&workYears=' + this.searchValue.workYears;
                    }
                } else {
                    if (this.keyword) {
                        url += '&keyword=' + this.keyword;
                    }
                }
                this.getRequest(url).then(resp => {
                    this.loading = false;
                    if (resp) {
                        this.resumes = resp.data;
                        this.total = resp.total;
                    }
                });
            },
            currentChange(currentPage) {
                this.page = currentPage;
                this.initResumes();
            },
            sizeChange(currentSize) {
                this.size = currentSize;
                this.initResumes();
            },
            showAddResumeView() {
                this.title = '添加简历';
                this.resume = {
                    id: null,
                    name: '',
                    gender: '男',
                    age: 25,
                    education: '',
                    workYears: 0,
                    phone: '',
                    email: '',
                    expectedSalary: '',
                    jobIntention: '',
                    workExperience: '',
                    educationExperience: '',
                    attachmentUrl: '',
                    attachmentName: '',
                    remark: ''
                };
                this.fileList = [];
                this.dialogVisible = true;
            },
            showEditResumeView(data) {
                this.title = '编辑简历';
                this.resume = JSON.parse(JSON.stringify(data));
                this.fileList = [];
                if (this.resume.attachmentUrl) {
                    this.fileList = [{
                        name: this.resume.attachmentName,
                        url: this.resume.attachmentUrl
                    }];
                }
                this.dialogVisible = true;
            },
            showDetailView(data) {
                this.currentResume = JSON.parse(JSON.stringify(data));
                this.detailVisible = true;
            },
            doAddResume() {
                this.$refs.resumeForm.validate(valid => {
                    if (valid) {
                        if (this.resume.id) {
                            updateResume(this.resume).then(resp => {
                                if (resp.status === 200) {
                                    this.dialogVisible = false;
                                    this.initResumes();
                                }
                            });
                        } else {
                            addResume(this.resume).then(resp => {
                                if (resp.status === 200) {
                                    this.dialogVisible = false;
                                    this.initResumes();
                                }
                            });
                        }
                    }
                });
            },
            deleteResume(data) {
                this.$confirm('此操作将永久删除该简历, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    deleteResume(data.id).then(resp => {
                        if (resp.status === 200) {
                            this.initResumes();
                        }
                    });
                }).catch(() => {
                    this.$message.info('已取消删除');
                });
            },
            handleArrangeInterview(data) {
                this.$router.push({
                    path: '/recruit/interview',
                    query: {resumeId: data.id, candidateName: data.name}
                });
            },
            handleUploadSuccess(response) {
                this.resume.attachmentUrl = response.obj;
                this.resume.attachmentName = response.msg;
            },
            handleRemove() {
                this.resume.attachmentUrl = '';
                this.resume.attachmentName = '';
            },
            beforeUpload() {
                this.importDataBtnText = '正在导入';
                this.importDataBtnIcon = 'el-icon-loading';
                this.importDataDisabled = true;
            },
            onSuccess() {
                this.importDataBtnText = '导入数据';
                this.importDataBtnIcon = 'el-icon-upload2';
                this.importDataDisabled = false;
                this.initResumes();
            },
            onError() {
                this.importDataBtnText = '导入数据';
                this.importDataBtnIcon = 'el-icon-upload2';
                this.importDataDisabled = false;
            },
            exportData() {
                window.open('/recruit/resume/export', '_parent');
            },
            resetSearch() {
                this.searchValue = {
                    education: '',
                    status: null,
                    workYears: null
                };
                this.showAdvanceSearchView = false;
                this.initResumes();
            },
            getStatusType(status) {
                const types = ['info', 'primary', 'warning', 'danger', 'success'];
                return types[status] || 'info';
            },
            getStatusText(status) {
                const texts = ['待筛选', '已筛选', '待面试', '已拒绝', '已录用'];
                return texts[status] || '未知';
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
