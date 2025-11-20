package com.romix.feature.feat26

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat26Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat26UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat26FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat26UserSummary
)

data class Feat26UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat26NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat26Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat26Config = Feat26Config()
) {

    fun loadSnapshot(userId: Long): Feat26NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat26NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat26UserSummary {
        return Feat26UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat26FeedItem> {
        val result = java.util.ArrayList<Feat26FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat26FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat26UiMapper {

    fun mapToUi(model: List<Feat26FeedItem>): Feat26UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat26UiModel(
            header = UiText("Feat26 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat26UiModel =
        Feat26UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat26UiModel =
        Feat26UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat26UiModel =
        Feat26UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat26Service(
    private val repository: Feat26Repository,
    private val uiMapper: Feat26UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat26UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat26UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat26UserItem1(val user: CoreUser, val label: String)
data class Feat26UserItem2(val user: CoreUser, val label: String)
data class Feat26UserItem3(val user: CoreUser, val label: String)
data class Feat26UserItem4(val user: CoreUser, val label: String)
data class Feat26UserItem5(val user: CoreUser, val label: String)
data class Feat26UserItem6(val user: CoreUser, val label: String)
data class Feat26UserItem7(val user: CoreUser, val label: String)
data class Feat26UserItem8(val user: CoreUser, val label: String)
data class Feat26UserItem9(val user: CoreUser, val label: String)
data class Feat26UserItem10(val user: CoreUser, val label: String)

data class Feat26StateBlock1(val state: Feat26UiModel, val checksum: Int)
data class Feat26StateBlock2(val state: Feat26UiModel, val checksum: Int)
data class Feat26StateBlock3(val state: Feat26UiModel, val checksum: Int)
data class Feat26StateBlock4(val state: Feat26UiModel, val checksum: Int)
data class Feat26StateBlock5(val state: Feat26UiModel, val checksum: Int)
data class Feat26StateBlock6(val state: Feat26UiModel, val checksum: Int)
data class Feat26StateBlock7(val state: Feat26UiModel, val checksum: Int)
data class Feat26StateBlock8(val state: Feat26UiModel, val checksum: Int)
data class Feat26StateBlock9(val state: Feat26UiModel, val checksum: Int)
data class Feat26StateBlock10(val state: Feat26UiModel, val checksum: Int)

fun buildFeat26UserItem(user: CoreUser, index: Int): Feat26UserItem1 {
    return Feat26UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat26StateBlock(model: Feat26UiModel): Feat26StateBlock1 {
    return Feat26StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat26UserSummary> {
    val list = java.util.ArrayList<Feat26UserSummary>(users.size)
    for (user in users) {
        list += Feat26UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat26UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat26UiModel {
    val summaries = (0 until count).map {
        Feat26UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat26UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat26UiModel> {
    val models = java.util.ArrayList<Feat26UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat26AnalyticsEvent1(val name: String, val value: String)
data class Feat26AnalyticsEvent2(val name: String, val value: String)
data class Feat26AnalyticsEvent3(val name: String, val value: String)
data class Feat26AnalyticsEvent4(val name: String, val value: String)
data class Feat26AnalyticsEvent5(val name: String, val value: String)
data class Feat26AnalyticsEvent6(val name: String, val value: String)
data class Feat26AnalyticsEvent7(val name: String, val value: String)
data class Feat26AnalyticsEvent8(val name: String, val value: String)
data class Feat26AnalyticsEvent9(val name: String, val value: String)
data class Feat26AnalyticsEvent10(val name: String, val value: String)

fun logFeat26Event1(event: Feat26AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat26Event2(event: Feat26AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat26Event3(event: Feat26AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat26Event4(event: Feat26AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat26Event5(event: Feat26AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat26Event6(event: Feat26AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat26Event7(event: Feat26AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat26Event8(event: Feat26AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat26Event9(event: Feat26AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat26Event10(event: Feat26AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat26Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat26Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat26Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat26Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat26Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat26Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat26Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat26Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat26Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat26Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat26(u: CoreUser): Feat26Projection1 =
    Feat26Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat26Projection1> {
    val list = java.util.ArrayList<Feat26Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat26(u)
    }
    return list
}
