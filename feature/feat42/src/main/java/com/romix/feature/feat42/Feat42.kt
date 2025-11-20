package com.romix.feature.feat42

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat42Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat42UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat42FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat42UserSummary
)

data class Feat42UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat42NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat42Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat42Config = Feat42Config()
) {

    fun loadSnapshot(userId: Long): Feat42NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat42NetworkSnapshot(
            users = listOf(user),
            posts = posts,
            rawHash = hash
        )
    }

    private fun snapshotChecksum(user: ApiUserDto, posts: List<ApiPostDto>): Int {
        var result = 1
        result = 31 * result + user.id.hashCode()
        result = 31 * result + user.name.hashCode()
        for (post in posts) {
            result = 31 * result + post.id.hashCode()
            result = 31 * result + post.title.hashCode()
        }
        return result
    }

    fun toUserSummary(coreUser: CoreUser): Feat42UserSummary {
        return Feat42UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat42FeedItem> {
        val result = java.util.ArrayList<Feat42FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat42FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat42UiMapper {

    fun mapToUi(model: List<Feat42FeedItem>): Feat42UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat42UiModel(
            header = UiText("Feat42 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat42UiModel =
        Feat42UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat42UiModel =
        Feat42UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat42UiModel =
        Feat42UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat42Service(
    private val repository: Feat42Repository,
    private val uiMapper: Feat42UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat42UiModel {
        val snapshot = repository.loadSnapshot(userId)
        if (snapshot.users.isEmpty()) {
            return uiMapper.emptyState()
        }
        val coreUser = CoreUser(
            id = snapshot.users[0].id,
            name = snapshot.users[0].name,
            email = null,
            isActive = true
        )
        val items = repository.toFeedItems(listOf(coreUser))
        return uiMapper.mapToUi(items)
    }

    fun ping(path: String): Int {
        val request = NetworkRequest(
            path = path,
            method = "GET",
            body = null
        )
        val response = networkClient.execute(request)
        return response.code
    }

    fun demoComplexFlow(usersCount: Int): Feat42UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat42UserItem1(val user: CoreUser, val label: String)
data class Feat42UserItem2(val user: CoreUser, val label: String)
data class Feat42UserItem3(val user: CoreUser, val label: String)
data class Feat42UserItem4(val user: CoreUser, val label: String)
data class Feat42UserItem5(val user: CoreUser, val label: String)
data class Feat42UserItem6(val user: CoreUser, val label: String)
data class Feat42UserItem7(val user: CoreUser, val label: String)
data class Feat42UserItem8(val user: CoreUser, val label: String)
data class Feat42UserItem9(val user: CoreUser, val label: String)
data class Feat42UserItem10(val user: CoreUser, val label: String)

data class Feat42StateBlock1(val state: Feat42UiModel, val checksum: Int)
data class Feat42StateBlock2(val state: Feat42UiModel, val checksum: Int)
data class Feat42StateBlock3(val state: Feat42UiModel, val checksum: Int)
data class Feat42StateBlock4(val state: Feat42UiModel, val checksum: Int)
data class Feat42StateBlock5(val state: Feat42UiModel, val checksum: Int)
data class Feat42StateBlock6(val state: Feat42UiModel, val checksum: Int)
data class Feat42StateBlock7(val state: Feat42UiModel, val checksum: Int)
data class Feat42StateBlock8(val state: Feat42UiModel, val checksum: Int)
data class Feat42StateBlock9(val state: Feat42UiModel, val checksum: Int)
data class Feat42StateBlock10(val state: Feat42UiModel, val checksum: Int)

fun buildFeat42UserItem(user: CoreUser, index: Int): Feat42UserItem1 {
    return Feat42UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat42StateBlock(model: Feat42UiModel): Feat42StateBlock1 {
    return Feat42StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat42UserSummary> {
    val list = java.util.ArrayList<Feat42UserSummary>(users.size)
    for (user in users) {
        list += Feat42UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat42UserSummary>): List<UiListItem> {
    val items = java.util.ArrayList<UiListItem>(summaries.size)
    for ((index, summary) in summaries.withIndex()) {
        items += UiListItem(
            id = index.toLong(),
            title = summary.name,
            subtitle = if (summary.isActive) "Active" else "Inactive",
            selected = summary.isActive
        )
    }
    return items
}

fun createLargeUiModel(count: Int): Feat42UiModel {
    val summaries = (0 until count).map {
        Feat42UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat42UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = java.util.ArrayList<CoreUser>(count)
    for (i in 0 until count) {
        list += CoreUser(
            id = i.toLong(),
            name = "User-$i",
            email = null,
            isActive = i % 3 != 0
        )
    }
    return list
}

fun mapToUiTextList(users: List<CoreUser>): List<UiText> {
    val list = java.util.ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat42UiModel> {
    val models = java.util.ArrayList<Feat42UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat42AnalyticsEvent1(val name: String, val value: String)
data class Feat42AnalyticsEvent2(val name: String, val value: String)
data class Feat42AnalyticsEvent3(val name: String, val value: String)
data class Feat42AnalyticsEvent4(val name: String, val value: String)
data class Feat42AnalyticsEvent5(val name: String, val value: String)
data class Feat42AnalyticsEvent6(val name: String, val value: String)
data class Feat42AnalyticsEvent7(val name: String, val value: String)
data class Feat42AnalyticsEvent8(val name: String, val value: String)
data class Feat42AnalyticsEvent9(val name: String, val value: String)
data class Feat42AnalyticsEvent10(val name: String, val value: String)

fun logFeat42Event1(event: Feat42AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat42Event2(event: Feat42AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat42Event3(event: Feat42AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat42Event4(event: Feat42AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat42Event5(event: Feat42AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat42Event6(event: Feat42AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat42Event7(event: Feat42AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat42Event8(event: Feat42AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat42Event9(event: Feat42AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat42Event10(event: Feat42AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat42Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat42Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat42Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat42Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat42Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat42Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat42Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat42Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat42Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat42Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat42(u: CoreUser): Feat42Projection1 =
    Feat42Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat42Projection1> {
    val list = java.util.ArrayList<Feat42Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat42(u)
    }
    return list
}
