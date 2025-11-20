package com.romix.feature.feat216

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat216Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat216UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat216FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat216UserSummary
)

data class Feat216UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat216NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat216Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat216Config = Feat216Config()
) {

    fun loadSnapshot(userId: Long): Feat216NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat216NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat216UserSummary {
        return Feat216UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat216FeedItem> {
        val result = java.util.ArrayList<Feat216FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat216FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat216UiMapper {

    fun mapToUi(model: List<Feat216FeedItem>): Feat216UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat216UiModel(
            header = UiText("Feat216 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat216UiModel =
        Feat216UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat216UiModel =
        Feat216UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat216UiModel =
        Feat216UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat216Service(
    private val repository: Feat216Repository,
    private val uiMapper: Feat216UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat216UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat216UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat216UserItem1(val user: CoreUser, val label: String)
data class Feat216UserItem2(val user: CoreUser, val label: String)
data class Feat216UserItem3(val user: CoreUser, val label: String)
data class Feat216UserItem4(val user: CoreUser, val label: String)
data class Feat216UserItem5(val user: CoreUser, val label: String)
data class Feat216UserItem6(val user: CoreUser, val label: String)
data class Feat216UserItem7(val user: CoreUser, val label: String)
data class Feat216UserItem8(val user: CoreUser, val label: String)
data class Feat216UserItem9(val user: CoreUser, val label: String)
data class Feat216UserItem10(val user: CoreUser, val label: String)

data class Feat216StateBlock1(val state: Feat216UiModel, val checksum: Int)
data class Feat216StateBlock2(val state: Feat216UiModel, val checksum: Int)
data class Feat216StateBlock3(val state: Feat216UiModel, val checksum: Int)
data class Feat216StateBlock4(val state: Feat216UiModel, val checksum: Int)
data class Feat216StateBlock5(val state: Feat216UiModel, val checksum: Int)
data class Feat216StateBlock6(val state: Feat216UiModel, val checksum: Int)
data class Feat216StateBlock7(val state: Feat216UiModel, val checksum: Int)
data class Feat216StateBlock8(val state: Feat216UiModel, val checksum: Int)
data class Feat216StateBlock9(val state: Feat216UiModel, val checksum: Int)
data class Feat216StateBlock10(val state: Feat216UiModel, val checksum: Int)

fun buildFeat216UserItem(user: CoreUser, index: Int): Feat216UserItem1 {
    return Feat216UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat216StateBlock(model: Feat216UiModel): Feat216StateBlock1 {
    return Feat216StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat216UserSummary> {
    val list = java.util.ArrayList<Feat216UserSummary>(users.size)
    for (user in users) {
        list += Feat216UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat216UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat216UiModel {
    val summaries = (0 until count).map {
        Feat216UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat216UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat216UiModel> {
    val models = java.util.ArrayList<Feat216UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat216AnalyticsEvent1(val name: String, val value: String)
data class Feat216AnalyticsEvent2(val name: String, val value: String)
data class Feat216AnalyticsEvent3(val name: String, val value: String)
data class Feat216AnalyticsEvent4(val name: String, val value: String)
data class Feat216AnalyticsEvent5(val name: String, val value: String)
data class Feat216AnalyticsEvent6(val name: String, val value: String)
data class Feat216AnalyticsEvent7(val name: String, val value: String)
data class Feat216AnalyticsEvent8(val name: String, val value: String)
data class Feat216AnalyticsEvent9(val name: String, val value: String)
data class Feat216AnalyticsEvent10(val name: String, val value: String)

fun logFeat216Event1(event: Feat216AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat216Event2(event: Feat216AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat216Event3(event: Feat216AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat216Event4(event: Feat216AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat216Event5(event: Feat216AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat216Event6(event: Feat216AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat216Event7(event: Feat216AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat216Event8(event: Feat216AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat216Event9(event: Feat216AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat216Event10(event: Feat216AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat216Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat216Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat216Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat216Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat216Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat216Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat216Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat216Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat216Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat216Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat216(u: CoreUser): Feat216Projection1 =
    Feat216Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat216Projection1> {
    val list = java.util.ArrayList<Feat216Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat216(u)
    }
    return list
}
