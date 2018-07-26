package io.choerodon.test.manager.app.service.impl;

import io.choerodon.agile.api.dto.IssueListDTO;
import io.choerodon.agile.api.dto.SearchDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.test.manager.api.dto.IssueInfosDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseDefectRelDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseStepDTO;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.app.service.TestCycleCaseDefectRelService;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseDefectRelE;
import io.choerodon.test.manager.domain.service.ITestCycleCaseDefectRelService;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseStepE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseDefectRelEFactory;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseStepEFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class TestCycleCaseDefectRelServiceImpl implements TestCycleCaseDefectRelService {
    @Autowired
    ITestCycleCaseDefectRelService iTestCycleCaseDefectRelService;

    @Autowired
	TestCaseService testCaseService;

	@Transactional(rollbackFor = Exception.class)
	@Override
	public TestCycleCaseDefectRelDTO insert(TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO, Long projectId) {
		return ConvertHelper.convert(iTestCycleCaseDefectRelService.insert(ConvertHelper.convert(testCycleCaseDefectRelDTO, TestCycleCaseDefectRelE.class)), TestCycleCaseDefectRelDTO.class);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void delete(TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO, Long projectId) {
		iTestCycleCaseDefectRelService.delete(ConvertHelper.convert(testCycleCaseDefectRelDTO, TestCycleCaseDefectRelE.class));

	}

    @Override
    public List<TestCycleCaseDefectRelDTO> query(TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO) {
        List<TestCycleCaseDefectRelE> serviceEPage = iTestCycleCaseDefectRelService.query(ConvertHelper.convert(testCycleCaseDefectRelDTO, TestCycleCaseDefectRelE.class));
        return ConvertHelper.convertList(serviceEPage, TestCycleCaseDefectRelDTO.class);
    }


	@Override
	public void populateDefectInfo(List<TestCycleCaseDefectRelDTO> lists, Long projectId) {
		if(!(lists !=null && !lists.isEmpty())){
			return;
		}
		Long[] issueLists = lists.stream().map(v -> v.getIssueId()).filter(u->u!=null).toArray(Long[]::new);
		if (issueLists.length==0) {
			return;
		}

		Map<Long, IssueInfosDTO> defectMap =testCaseService.getIssueInfoMap(projectId,issueLists);
		lists.forEach(v -> {
			v.setIssueInfosDTO(defectMap.get(v.getIssueId()));
		});
	}

	@Override
	public void populateCycleCaseDefectInfo(List<TestCycleCaseDTO> testCycleCaseDTOS, Long projectId){
		List<TestCycleCaseDefectRelDTO> list = new ArrayList<>();
		for (TestCycleCaseDTO v : testCycleCaseDTOS) {
			List<TestCycleCaseDefectRelDTO> defects = v.getDefects();
			list.addAll(defects);
		}
		populateDefectInfo(list,projectId);
	}

	@Override
	public void populateCaseStepDefectInfo(List<TestCycleCaseStepDTO> testCycleCaseDTOS, Long projectId){
		List<TestCycleCaseDefectRelDTO> list = new ArrayList<>();
		for (TestCycleCaseStepDTO v : testCycleCaseDTOS) {
			List<TestCycleCaseDefectRelDTO> defects = v.getDefects();
			list.addAll(defects);
		}
		populateDefectInfo(list,projectId);
	}

	@Override
	public List<TestCycleCaseDefectRelDTO> getSubCycleStepsHaveDefect(Long cycleCaseId) {
		TestCycleCaseStepE caseStepE = TestCycleCaseStepEFactory.create();
		caseStepE.setExecuteId(cycleCaseId);
		List<TestCycleCaseStepE> caseStepES = caseStepE.querySelf();
		List<TestCycleCaseDefectRelE> defectRelES = new ArrayList<>();
		caseStepES.stream().forEach(v -> {
			Optional.ofNullable(cycleStepHaveDefect(v.getExecuteStepId())).ifPresent(u -> defectRelES.addAll(u));
		});
		return ConvertHelper.convertList(defectRelES,TestCycleCaseDefectRelDTO.class);
	}

	private List<TestCycleCaseDefectRelE> cycleStepHaveDefect(Long cycleStepId) {
		TestCycleCaseDefectRelE caseDefectRelE = TestCycleCaseDefectRelEFactory.create();
		caseDefectRelE.setDefectLinkId(cycleStepId);
		caseDefectRelE.setDefectType(TestCycleCaseDefectRelE.CASE_STEP);
		return caseDefectRelE.querySelf();

	}

}
