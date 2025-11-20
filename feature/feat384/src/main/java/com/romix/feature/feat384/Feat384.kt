package com.romix.feature.feat384

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat384Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat384UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat384FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat384UserSummary
)

data class Feat384UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat384NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat384Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat384Config = Feat384Config()
) {

    fun loadSnapshot(userId: Long): Feat384NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat384NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat384UserSummary {
        return Feat384UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat384FeedItem> {
        val result = java.util.ArrayList<Feat384FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat384FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat384UiMapper {

    fun mapToUi(model: List<Feat384FeedItem>): Feat384UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat384UiModel(
            header = UiText("Feat384 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat384UiModel =
        Feat384UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat384UiModel =
        Feat384UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat384UiModel =
        Feat384UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat384Service(
    private val repository: Feat384Repository,
    private val uiMapper: Feat384UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat384UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat384UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat384UserItem1(val user: CoreUser, val label: String)
data class Feat384UserItem2(val user: CoreUser, val label: String)
data class Feat384UserItem3(val user: CoreUser, val label: String)
data class Feat384UserItem4(val user: CoreUser, val label: String)
data class Feat384UserItem5(val user: CoreUser, val label: String)
data class Feat384UserItem6(val user: CoreUser, val label: String)
data class Feat384UserItem7(val user: CoreUser, val label: String)
data class Feat384UserItem8(val user: CoreUser, val label: String)
data class Feat384UserItem9(val user: CoreUser, val label: String)
data class Feat384UserItem10(val user: CoreUser, val label: String)

data class Feat384StateBlock1(val state: Feat384UiModel, val checksum: Int)
data class Feat384StateBlock2(val state: Feat384UiModel, val checksum: Int)
data class Feat384StateBlock3(val state: Feat384UiModel, val checksum: Int)
data class Feat384StateBlock4(val state: Feat384UiModel, val checksum: Int)
data class Feat384StateBlock5(val state: Feat384UiModel, val checksum: Int)
data class Feat384StateBlock6(val state: Feat384UiModel, val checksum: Int)
data class Feat384StateBlock7(val state: Feat384UiModel, val checksum: Int)
data class Feat384StateBlock8(val state: Feat384UiModel, val checksum: Int)
data class Feat384StateBlock9(val state: Feat384UiModel, val checksum: Int)
data class Feat384StateBlock10(val state: Feat384UiModel, val checksum: Int)

fun buildFeat384UserItem(user: CoreUser, index: Int): Feat384UserItem1 {
    return Feat384UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat384StateBlock(model: Feat384UiModel): Feat384StateBlock1 {
    return Feat384StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat384UserSummary> {
    val list = java.util.ArrayList<Feat384UserSummary>(users.size)
    for (user in users) {
        list += Feat384UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat384UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat384UiModel {
    val summaries = (0 until count).map {
        Feat384UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat384UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat384UiModel> {
    val models = java.util.ArrayList<Feat384UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat384AnalyticsEvent1(val name: String, val value: String)
data class Feat384AnalyticsEvent2(val name: String, val value: String)
data class Feat384AnalyticsEvent3(val name: String, val value: String)
data class Feat384AnalyticsEvent4(val name: String, val value: String)
data class Feat384AnalyticsEvent5(val name: String, val value: String)
data class Feat384AnalyticsEvent6(val name: String, val value: String)
data class Feat384AnalyticsEvent7(val name: String, val value: String)
data class Feat384AnalyticsEvent8(val name: String, val value: String)
data class Feat384AnalyticsEvent9(val name: String, val value: String)
data class Feat384AnalyticsEvent10(val name: String, val value: String)

fun logFeat384Event1(event: Feat384AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat384Event2(event: Feat384AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat384Event3(event: Feat384AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat384Event4(event: Feat384AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat384Event5(event: Feat384AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat384Event6(event: Feat384AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat384Event7(event: Feat384AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat384Event8(event: Feat384AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat384Event9(event: Feat384AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat384Event10(event: Feat384AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat384Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat384Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat384Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat384Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat384Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat384Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat384Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat384Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat384Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat384Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat384(u: CoreUser): Feat384Projection1 =
    Feat384Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat384Projection1> {
    val list = java.util.ArrayList<Feat384Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat384(u)
    }
    return list
}
