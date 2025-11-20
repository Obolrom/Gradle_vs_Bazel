package com.romix.feature.feat231

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat231Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat231UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat231FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat231UserSummary
)

data class Feat231UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat231NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat231Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat231Config = Feat231Config()
) {

    fun loadSnapshot(userId: Long): Feat231NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat231NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat231UserSummary {
        return Feat231UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat231FeedItem> {
        val result = java.util.ArrayList<Feat231FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat231FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat231UiMapper {

    fun mapToUi(model: List<Feat231FeedItem>): Feat231UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat231UiModel(
            header = UiText("Feat231 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat231UiModel =
        Feat231UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat231UiModel =
        Feat231UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat231UiModel =
        Feat231UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat231Service(
    private val repository: Feat231Repository,
    private val uiMapper: Feat231UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat231UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat231UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat231UserItem1(val user: CoreUser, val label: String)
data class Feat231UserItem2(val user: CoreUser, val label: String)
data class Feat231UserItem3(val user: CoreUser, val label: String)
data class Feat231UserItem4(val user: CoreUser, val label: String)
data class Feat231UserItem5(val user: CoreUser, val label: String)
data class Feat231UserItem6(val user: CoreUser, val label: String)
data class Feat231UserItem7(val user: CoreUser, val label: String)
data class Feat231UserItem8(val user: CoreUser, val label: String)
data class Feat231UserItem9(val user: CoreUser, val label: String)
data class Feat231UserItem10(val user: CoreUser, val label: String)

data class Feat231StateBlock1(val state: Feat231UiModel, val checksum: Int)
data class Feat231StateBlock2(val state: Feat231UiModel, val checksum: Int)
data class Feat231StateBlock3(val state: Feat231UiModel, val checksum: Int)
data class Feat231StateBlock4(val state: Feat231UiModel, val checksum: Int)
data class Feat231StateBlock5(val state: Feat231UiModel, val checksum: Int)
data class Feat231StateBlock6(val state: Feat231UiModel, val checksum: Int)
data class Feat231StateBlock7(val state: Feat231UiModel, val checksum: Int)
data class Feat231StateBlock8(val state: Feat231UiModel, val checksum: Int)
data class Feat231StateBlock9(val state: Feat231UiModel, val checksum: Int)
data class Feat231StateBlock10(val state: Feat231UiModel, val checksum: Int)

fun buildFeat231UserItem(user: CoreUser, index: Int): Feat231UserItem1 {
    return Feat231UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat231StateBlock(model: Feat231UiModel): Feat231StateBlock1 {
    return Feat231StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat231UserSummary> {
    val list = java.util.ArrayList<Feat231UserSummary>(users.size)
    for (user in users) {
        list += Feat231UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat231UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat231UiModel {
    val summaries = (0 until count).map {
        Feat231UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat231UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat231UiModel> {
    val models = java.util.ArrayList<Feat231UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat231AnalyticsEvent1(val name: String, val value: String)
data class Feat231AnalyticsEvent2(val name: String, val value: String)
data class Feat231AnalyticsEvent3(val name: String, val value: String)
data class Feat231AnalyticsEvent4(val name: String, val value: String)
data class Feat231AnalyticsEvent5(val name: String, val value: String)
data class Feat231AnalyticsEvent6(val name: String, val value: String)
data class Feat231AnalyticsEvent7(val name: String, val value: String)
data class Feat231AnalyticsEvent8(val name: String, val value: String)
data class Feat231AnalyticsEvent9(val name: String, val value: String)
data class Feat231AnalyticsEvent10(val name: String, val value: String)

fun logFeat231Event1(event: Feat231AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat231Event2(event: Feat231AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat231Event3(event: Feat231AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat231Event4(event: Feat231AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat231Event5(event: Feat231AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat231Event6(event: Feat231AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat231Event7(event: Feat231AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat231Event8(event: Feat231AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat231Event9(event: Feat231AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat231Event10(event: Feat231AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat231Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat231Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat231Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat231Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat231Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat231Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat231Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat231Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat231Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat231Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat231(u: CoreUser): Feat231Projection1 =
    Feat231Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat231Projection1> {
    val list = java.util.ArrayList<Feat231Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat231(u)
    }
    return list
}
