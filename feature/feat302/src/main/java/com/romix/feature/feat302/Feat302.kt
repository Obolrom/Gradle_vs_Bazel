package com.romix.feature.feat302

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat302Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat302UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat302FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat302UserSummary
)

data class Feat302UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat302NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat302Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat302Config = Feat302Config()
) {

    fun loadSnapshot(userId: Long): Feat302NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat302NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat302UserSummary {
        return Feat302UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat302FeedItem> {
        val result = java.util.ArrayList<Feat302FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat302FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat302UiMapper {

    fun mapToUi(model: List<Feat302FeedItem>): Feat302UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat302UiModel(
            header = UiText("Feat302 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat302UiModel =
        Feat302UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat302UiModel =
        Feat302UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat302UiModel =
        Feat302UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat302Service(
    private val repository: Feat302Repository,
    private val uiMapper: Feat302UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat302UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat302UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat302UserItem1(val user: CoreUser, val label: String)
data class Feat302UserItem2(val user: CoreUser, val label: String)
data class Feat302UserItem3(val user: CoreUser, val label: String)
data class Feat302UserItem4(val user: CoreUser, val label: String)
data class Feat302UserItem5(val user: CoreUser, val label: String)
data class Feat302UserItem6(val user: CoreUser, val label: String)
data class Feat302UserItem7(val user: CoreUser, val label: String)
data class Feat302UserItem8(val user: CoreUser, val label: String)
data class Feat302UserItem9(val user: CoreUser, val label: String)
data class Feat302UserItem10(val user: CoreUser, val label: String)

data class Feat302StateBlock1(val state: Feat302UiModel, val checksum: Int)
data class Feat302StateBlock2(val state: Feat302UiModel, val checksum: Int)
data class Feat302StateBlock3(val state: Feat302UiModel, val checksum: Int)
data class Feat302StateBlock4(val state: Feat302UiModel, val checksum: Int)
data class Feat302StateBlock5(val state: Feat302UiModel, val checksum: Int)
data class Feat302StateBlock6(val state: Feat302UiModel, val checksum: Int)
data class Feat302StateBlock7(val state: Feat302UiModel, val checksum: Int)
data class Feat302StateBlock8(val state: Feat302UiModel, val checksum: Int)
data class Feat302StateBlock9(val state: Feat302UiModel, val checksum: Int)
data class Feat302StateBlock10(val state: Feat302UiModel, val checksum: Int)

fun buildFeat302UserItem(user: CoreUser, index: Int): Feat302UserItem1 {
    return Feat302UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat302StateBlock(model: Feat302UiModel): Feat302StateBlock1 {
    return Feat302StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat302UserSummary> {
    val list = java.util.ArrayList<Feat302UserSummary>(users.size)
    for (user in users) {
        list += Feat302UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat302UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat302UiModel {
    val summaries = (0 until count).map {
        Feat302UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat302UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat302UiModel> {
    val models = java.util.ArrayList<Feat302UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat302AnalyticsEvent1(val name: String, val value: String)
data class Feat302AnalyticsEvent2(val name: String, val value: String)
data class Feat302AnalyticsEvent3(val name: String, val value: String)
data class Feat302AnalyticsEvent4(val name: String, val value: String)
data class Feat302AnalyticsEvent5(val name: String, val value: String)
data class Feat302AnalyticsEvent6(val name: String, val value: String)
data class Feat302AnalyticsEvent7(val name: String, val value: String)
data class Feat302AnalyticsEvent8(val name: String, val value: String)
data class Feat302AnalyticsEvent9(val name: String, val value: String)
data class Feat302AnalyticsEvent10(val name: String, val value: String)

fun logFeat302Event1(event: Feat302AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat302Event2(event: Feat302AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat302Event3(event: Feat302AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat302Event4(event: Feat302AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat302Event5(event: Feat302AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat302Event6(event: Feat302AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat302Event7(event: Feat302AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat302Event8(event: Feat302AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat302Event9(event: Feat302AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat302Event10(event: Feat302AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat302Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat302Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat302Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat302Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat302Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat302Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat302Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat302Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat302Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat302Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat302(u: CoreUser): Feat302Projection1 =
    Feat302Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat302Projection1> {
    val list = java.util.ArrayList<Feat302Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat302(u)
    }
    return list
}
